# Лабораторна робота 2. Обробка XML-документів

**Варіант:** 9. Цукерки  
**Корінь XML:** `Candy`  
**Мова:** Java 21  
**Збірка:** Maven  
**Репозиторій:** https://github.com/zekapele/secondLab

## 1. Мета роботи

Опанувати опис даних у XML, обмеження структури схемою XSD, розбір документа парсерами SAX, DOM і StAX, валідацію, сортування колекції об’єктів та перетворення XML за допомогою XSL.

## 2. Постановка задачі

1. Створити XML-файл каталогу цукерок і відповідну схему XSD.
2. У схемі використати прості та комплексні типи, переліки, граничні значення, обов’язкові атрибути та тип `ID`.
3. Створити Java-класи, що відповідають цьому опису.
4. Зчитати XML у колекцію об’єктів трьома парсерами: SAX, DOM, StAX.
5. Сортувати об’єкти через інтерфейс `Comparator`.
6. Валідувати XML за XSD.
7. XSL-перетворенням розбити плоский список на підсписки за характеристикою (тип цукерки).
8. Уникати дублювання коду.
9. Покрити код unit-тестами.
10. Виводити хід роботи через налаштований Logger.

Предметна область (варіант «Цукерки»):

| Поле | Зміст |
|---|---|
| Name | назва цукерки |
| Energy | калорійність, ккал |
| Type | карамель, ірис, шоколадна (з начинкою або ні) |
| Ingredients | вода, цукор (мг), фруктоза (мг), тип шоколаду, ванілін (мг) |
| Value | білки, жири, вуглеводи (г) |
| Production | підприємство-виробник |

## 3. Структура проєкту

```
secondLab/
├── pom.xml
├── src/main/java/secondlab/candy/
│   ├── Main.java                 точка входу
│   ├── model/                    Java-модель каталогу
│   ├── parser/                   SAX, DOM, StAX
│   ├── validation/               перевірка за XSD
│   ├── transform/                XSL
│   ├── sort/                     Comparator
│   ├── service/                  сценарій завантаження
│   └── logging/                  Logger
├── src/main/resources/
│   ├── xml/candies.xml           вхідний каталог
│   ├── xsd/candies.xsd           схема
│   ├── xsl/candies-by-type.xsl   групування за типом
│   └── logging.properties
├── src/test/java/                JUnit 5
└── output/candies-by-type.xml    результат XSL
```

## 4. XML і XSD

Вхідний документ: `src/main/resources/xml/candies.xml`.  
Схема: `src/main/resources/xsd/candies.xsd`.

У схемі:

- **прості типи:** `Label`, `Energy` (1–1000 ккал), `Milligrams`, `Vanillin`, `Grams` (0–100, 2 знаки);
- **переліки:** `caramel | iris | chocolate`, `milk | dark | white`;
- **комплексні типи:** `CandyItem`, `CandyType`, `Ingredients`, `NutritionalValue`;
- **обов’язкові атрибути:** `id` типу `xs:ID`, `filling` типу `boolean`.

У каталозі 7 цукерок: Вечірній Київ, Ромашка, Білочка, Стріла, Дюшес, Корівка, Пташине молоко.

## 5. Java-модель

Клас `Candy` (record) відповідає елементу `candy`:

- `id`, `name`, `energy`
- `type` — вид і наявність начинки
- `ingredients` — вода, цукор, фруктоза, тип шоколаду, ванілін
- `value` — білки, жири, вуглеводи
- `production`

Обмеження схеми перевіряються і в конструкторі моделі.

## 6. Парсери SAX, DOM, StAX

Усі три парсери реалізують `CandyParser` і збирають той самий список `List<Candy>` через спільний `CandyAssembler`. Це прибирає дублювання логіки полів.

| Парсер | Клас | Ідея |
|---|---|---|
| SAX | `SaxCandyParser` | події `startElement` / `endElement` |
| DOM | `DomCandyParser` | дерево `Document` |
| StAX | `StaxCandyParser` | курсор `XMLStreamReader` |

Програма читає документ кожним парсером. Результат однаковий: 7 об’єктів у порядку XML.

## 7. Сортування

Інтерфейс `Comparator`:

- `CandyEnergyComparator` — за калорійністю;
- `CandyNameComparator` — за українською назвою;
- `CandyProductionComparator` — за виробником, потім за назвою.

Після запуску в логу:

```
SAX by energy: Дюшес (365 kcal), Ромашка (378 kcal), Білочка (415 kcal),
               Корівка (440 kcal), Вечірній Київ (452 kcal),
               Пташине молоко (470 kcal), Стріла (498 kcal)
DOM by name:   Білочка, Вечірній Київ, Дюшес, Корівка,
               Пташине молоко, Ромашка, Стріла
```

SAX, DOM і StAX дають однаковий порядок за калорійністю.

## 8. Валідація

`XmlValidator` перевіряє байти XML проти XSD (`javax.xml.validation`).  
Невалідний документ (немає `id`, енергія поза 1–1000, невідомий тип тощо) не розбирається.

Успішний запуск:

```
INFO XmlValidator - XML document is valid
```

## 9. XSL-перетворення

Файл `src/main/resources/xsl/candies-by-type.xsl` групує цукерки за `type/kind`.

**Було** (плоский список у `candies.xml`):

```xml
<Candy>
    <candy>...</candy>
    <candy>...</candy>
    ...
</Candy>
```

**Стало** (`output/candies-by-type.xml`):

```xml
<Candy>
    <caramel>
        <candy id="romashka">Ромашка</candy>
        <candy id="duchess">Дюшес</candy>
    </caramel>
    <chocolate>
        <candy id="vk">Вечірній Київ</candy>
        <candy id="strila">Стріла</candy>
        <candy id="ptashyne">Пташине молоко</candy>
    </chocolate>
    <iris>
        <candy id="bilochka">Білочка</candy>
        <candy id="korivka">Корівка</candy>
    </iris>
</Candy>
```

Це відповідає прикладу з методички: список елементів розбито на підсписки за характеристикою Type.

## 10. Логування

Використано `java.util.logging`.

- конфігурація: `src/main/resources/logging.properties`
- вивід у stdout: `StdOutHandler` (щоб повідомлення були в консолі IntelliJ, а не в stderr)

Формат рядка: дата, час, рівень, клас, повідомлення.

## 11. Тести

JUnit 5, команда `mvn test`. Покрито:

- розбір SAX / DOM / StAX і збіг результатів;
- відхилення невалідного XML;
- валідацію XSD (межі, переліки, дублікати `id`);
- XSL-групування за типами;
- компаратори;
- обмеження моделі;
- сценарій `Main`.

## 12. Як запустити

Потрібні JDK 21 і Maven (або IntelliJ IDEA).

```bash
mvn test
mvn exec:java
```

У IntelliJ: відкрити `Main.java` → Run. Робоча директорія — корінь проєкту.

Після запуску:

1. У консолі — валідація, три парсери, сортування, повний згрупований XML.
2. На диску — `output/candies-by-type.xml`.

Порівняння для захисту:

- вхід: `src/main/resources/xml/candies.xml`
- вихід: `output/candies-by-type.xml`

## 13. Висновки

Реалізовано каталог цукерок як XML із жорсткою схемою XSD. Документ читається трьома стандартними парсерами Java в одну модель, перевіряється за схемою, сортується через `Comparator` і XSL перетворюється на XML, згрупований за типом цукерки. Повідомлення програми йдуть через Logger, логіка розбору не дублюється, поведінка перевірена unit-тестами.
