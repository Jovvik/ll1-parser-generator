Генератор нисходящих парсеров на kotlin по LL(1)-грамматикам с поддержкой синтезируемых и наследуемых атрибутов

Сделанные задания
- [x] (10 баллов) LL(1)-грамматики, нисходящий разбор
- [x] (10 баллов) поддержка синтезируемых атрибутов
- [x] (10 баллов) поддержка наследуемых атрибутов
- [x] (обязательно) сгенерировать с помощью вашего генератора калькулятор
- [x] (5 баллов) выполнить с помощью вашего генератора ваше задание второй лабораторной

Пример грамматики для генератора парсеров:
```
grammar fn

@header `import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode
import guru.nidi.graphviz.attribute.Label`

@fields `private var nodeCount = 0
private fun node(label: String) = nodeCount++.let { mutNode(nodeCount.toString()).add(Label.of(label)) }`

@start h

h ret MutableGraph:
    | `val g = mutGraph("fun").setDirected(true); val h=mutNode("h"); g.add(h)`
    FN `h.addLink(mutNode(fn))`
    NAME `h.addLink(mutNode(name))`
    LPAREN `h.addLink(mutNode(lparen))`
    p `h.addLink(p)`
    RPAREN `h.addLink(mutNode(rparen))`
    r `h.addLink(r); return g`


p ret MutableNode:
    | NAME t pprime `return node("P").addLink(node(name)).addLink(t).addLink(pprime)`
    | `return node("P")`

pprime ret MutableNode:
    | COMMA p `return node("P'").addLink(node(comma)).addLink(p)`
    | `return node("P'")`

t ret MutableNode:
    | COLON NAME `nodeCount++; return node("T").addLink(node(colon)).addLink(node(name))`

r ret MutableNode:
    | t `return node("R").addLink(t)`
    | `return node("R")`

WS: '[ \t\r\n]' skip
FN: 'fun'
LPAREN: '\('
RPAREN: '\)'
NAME: '[A-z]+'
COMMA: ','
COLON: ':'
```

- Блоки кода (на kotlin) пишутся в ``
- Терминалы - регулярные выражения в кавычках
- Можно определить терминал как пропускной (`skip`), тогда он будет игнорироваться лексером
- Тип синтезируемого атрибута пишется после названия правила как `ret тип`
- Заголовок (для импортов) определяется через `@header`
- Добавить код в тело класса можно через `@fields`
- Начальный нетерминал определяется как `@start имя_нетерминала`
- Правила вывода для нетерминалов пишутся через `|`
