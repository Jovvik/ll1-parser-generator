grammar Grammar;

@header {
import java.util.*;
}

file returns [Grammar grammar]: 'grammar' name=NAME header fields begin rules EOF
    { $grammar = new Grammar($name.text, $rules.rules_, $begin.t, $header.t, $fields.t); };

header returns [String t]: '@header' CODE { $t = $CODE.text; }
    | { $t = null; };

fields returns [String t]: '@fields' CODE { $t = $CODE.text; }
    | { $t = null; };

begin returns [String t]: '@start' NAME { $t = $NAME.text; };

rules returns [List<Rule> rules_]
    @init { $rules_ = new ArrayList<>(); }:
    (grammarRule { $rules_.add($grammarRule.rule_); })*;

grammarRule returns [Rule rule_]
    @init { String rettype = null; }:
    NAME arguments ('ret' returnType=NAME { rettype = $returnType.text; })? ':' productions
        { $rule_ = new NonTerminalRule($NAME.text, $arguments.args, $productions.prods, rettype); }
    | NAME ':' REGEX { $rule_ = new TerminalRule($NAME.text, $REGEX.text); }
    | NAME ':' REGEX 'skip' { $rule_ = new SkipRule($NAME.text, $REGEX.text); };

arguments returns [List<Argument> args]
    @init { $args = new ArrayList<>(); }:
    '(' (arg { $args.add($arg.arg_); })* ')'
    |;

arg returns [Argument arg_]:
    name=NAME ':' type=NAME { $arg_ = new Argument($name.text, $type.text); };

productions returns [List<Production> prods]
    @init { $prods = new ArrayList<>(); }:
    ('|' production { $prods.add($production.prod); })+;

production returns [Production prod]
    @init { List<Atom> atoms = new ArrayList<>(); String finalCode = null; }:
    (atom { atoms.add($atom.atom_); })* (code=CODE { finalCode = $code.text; })? { $prod = new Production(atoms, finalCode); };

atom returns [Atom atom_]
    @init { List<String> args = new ArrayList<>(); String code = null; }:
    (codeBlock=CODE { code = $codeBlock.text; })?
    NAME
    ('(' (arg_=NAME { args.add($arg_.text); })* ')')?
    { $atom_ = new Atom($NAME.text, args, code); }
    ;

WS: [\p{White_Space}] -> skip;
NAME: [A-z0-9]+;
REGEX: '\'' (~["])+? '\'';
CODE: '`' (~[`])+? '`';