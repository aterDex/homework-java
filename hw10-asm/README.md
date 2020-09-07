## hw10-asm

##AgentLogMethod
Создаем JavaAgent который выводит в консоль данные при вызове методов который помечены `ru.otus.homework.herald.api.@Log`.
Выводит имя переменных, работает в один проход.
Имена переменных берутся либо из секции property либо из секции localVariable.
Если из секции property вызов System.out.println будет в начале логируемого метода. 
Если из секции localVariable создается статический метод помошник для логирования.

##AgentInline
Создаем JavaAgent который выводит в консоль данные при вызове методов который помечены `@ru.otus.homework.herald.api.@Log`.
Агент может работать в двух режимах, с поиском информации по именам переменных так и без поиска.
По умолчанию агент не ищет имена переменных, для включения функции поиска в агент нужно передать параметр `rpn`
#### Пример вызова без поиска имен переменных
> -javaagent:hw10-asm-0.1-uber.jar
#### Пример вызова c поиском имен переменных
> -javaagent:hw10-asm-0.1-uber.jar=rpn