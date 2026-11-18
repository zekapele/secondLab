# Лабораторна робота 2. Обробка XML-документів

**Варіант:** 9. Цукерки  
**Корінь XML:** `Candy`  
**Мова:** Java 21  
**Збірка:** Maven  
**Репозиторій:** https://github.com/zekapele/secondLab

## 1. Мета роботи

Описати каталог цукерок у XML, обмежити структуру схемою XSD, зчитати документ у колекцію Java-об’єктів, відсортувати її та XSL-перетворенням розбити список на групи за типом цукерки.

## 2. Постановка задачі

1. XML-файл і відповідна схема XSD (прості та комплексні типи, переліки, граничні значення, обов’язкові атрибути, тип `ID`).
2. Java-класи під цей опис.
3. Розбір XML і ініціалізація колекції. Для читання вузлів використано SAX, DOM і StAX.
4. Сортування через `Comparator`.
5. Валідація XML за XSD.
6. XSL: плоский список розкладається на підсписки за Type.
7. Без дублювання коду.
8. Unit-тести.
9. Повідомлення через Logger.

Поля варіанта: Name, Energy (ккал), Type (карамель / ірис / шоколадна, з начинкою або ні), Ingredients (вода, цукор і фруктоза в мг, тип шоколаду, ванілін), Value (білки, жири, вуглеводи в г), Production.

## 3. Що змінено в реалізації розбору

Спочатку SAX, DOM і StAX були окремими парсерами, кожен зі своєю логікою збору об’єктів. Це дублювало обробник.

Після доопрацювання залишено **один парсер** і **один handler**:

| Клас | Роль |
|---|---|
| `XmlCandyParser` | єдиний парсер; будує колекцію `List<Candy>` |
| `CandyXmlHandler` | єдиний обробник; реагує на `startElement`, `characters`, `endElement` |

SAX, DOM і StAX більше не парсять «самі по собі». Вони лише постачають події в `CandyXmlHandler`. Яке джерело подій використати, задається в `XmlCandyParser` (за замовчуванням SAX).

`Main` запускає розбір **один раз**, через цей парсер. Колекція ініціалізується одним handler, а не трьома різними обробниками.

## 4. Структура проєкту

```
secondLab/
├── pom.xml
├── src/main/java/secondlab/candy/
│   ├── Main.java
│   ├── model/
│   ├── parser/          XmlCandyParser, CandyXmlHandler
│   ├── validation/
│   ├── transform/
│   ├── sort/
│   ├── service/
│   └── logging/
├── src/main/resources/
│   ├── xml/candies.xml
│   ├── xsd/candies.xsd
│   ├── xsl/candies-by-type.xsl
│   └── logging.properties
├── src/test/java/
└── output/candies-by-type.xml
```

## 5. XML і XSD

Вхід: `src/main/resources/xml/candies.xml`  
Схема: `src/main/resources/xsd/candies.xsd`

У схемі:

- прості типи: `Label`, `Energy` (1–1000 ккал), `Milligrams`, `Vanillin`, `Grams` (0–100, два знаки після коми);
- переліки: `caramel | iris | chocolate`, `milk | dark | white`;
- комплексні типи: `CandyItem`, `CandyType`, `Ingredients`, `NutritionalValue`;
- обов’язкові атрибути: `id` (`xs:ID`), `filling` (`boolean`).

У каталозі сім цукерок: Вечірній Київ, Ромашка, Білочка, Стріла, Дюшес, Корівка, Пташине молоко.

## 6. Java-модель

`Candy` відповідає елементу `candy`: `id`, `name`, `energy`, `type`, `ingredients`, `value`, `production`. Обмеження схеми перевіряються і в конструкторі.

## 7. Сортування

`Comparator`:

- `CandyEnergyComparator` — за калорійністю;
- `CandyNameComparator` — за українською назвою;
- `CandyProductionComparator` — за виробником, потім за назвою.

Приклад логу після запуску з SAX:

```
Collection built with SAX (7 candies)
SAX by energy: Дюшес (365 kcal), ..., Стріла (498 kcal)
SAX by name:   Білочка, ..., Стріла
```

## 8. Валідація

`XmlValidator` перевіряє XML за XSD до розбору. Невалідний документ (немає `id`, енергія поза межами, невідомий тип) не потрапляє в колекцію.

```
INFO XmlValidator - XML document is valid
```

## 9. XSL-перетворення

`src/main/resources/xsl/candies-by-type.xsl` групує записи за `type/kind`.

Вхід (`candies.xml`) — плоский список `<candy>`.  
Вихід (`output/candies-by-type.xml`) — підсписки:

```xml
<Candy>
    <caramel>   <!-- Ромашка, Дюшес -->
    <chocolate> <!-- Вечірній Київ, Стріла, Пташине молоко -->
    <iris>      <!-- Білочка, Корівка -->
</Candy>
```

## 10. Логування

`java.util.logging`, файл `logging.properties`. Повідомлення йдуть у стандартний вивід (`StdOutHandler`).

## 11. Тести

`mvn test` (JUnit 5): валідація XSD, один handler, джерела подій SAX/DOM/StAX дають однаковий каталог, компаратори, XSL, `Main`.

## 12. Запуск

JDK 21, Maven або IntelliJ IDEA.

```bash
mvn test
mvn exec:java
```

У IntelliJ: `Main.java` → Run, робоча директорія — корінь проєкту. Без аргументів використовується SAX. Інше джерело подій: `DOM` або `STAX` у Program arguments.

Результат:

1. консоль — валідація, один парсер, сортування, згрупований XML;
2. файл — `output/candies-by-type.xml`.

Для порівняння: вхід `src/main/resources/xml/candies.xml`, вихід `output/candies-by-type.xml`.

## 13. Висновки

Каталог цукерок описано в XML і обмежено XSD. Документ валідується, читається **одним парсером** `XmlCandyParser` з **одним обробником** `CandyXmlHandler`, сортується через `Comparator` і XSL перетворюється на XML, згрупований за типом. SAX, DOM і StAX лишаються як способи обходу документа, без окремих обробників. Логіка полів не дублюється, хід роботи пише Logger, поведінка перевірена тестами.
