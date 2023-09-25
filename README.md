# Elections kata

## Goal

You inherit this code base that is used to compute elections results in different ways.

You goal will be to avoid any potential fraud, and add the following features:
- Count as null a vote of an elector in another district
- Make sure nobody can vote twice.

## How you can use this kata

We have found this kata useful to:
- train on refactoring
- play with TCR (we recommand using this [awesome plugin](https://plugins.jetbrains.com/plugin/7655-limited-wip) for IntelliJ
- discuss on tests
- expriment on several designs


## Направления рефакторинга и ветки кода

- **logging-my-refactoring** - делал по одному рефакторингу на коммит, брал первый попавшийся "запах" и рефакторил его.
- **extracting-voting-strategy** - подготовительный рефакторинг для добавления нового способа голосования. Превращение
  класса `Election` в шаблонный класс, а реализация добавления кандидата, учета голоса, расчета результата перенесена в 
  классы конкретных стратегий.
- **five-lines-of-code** - попытка применять правила из книги "Пять строк кода".