# Sorting and Limiting Query Results

## Cursors

Cursor to referencja do zbioru wyników zapytania.\
Metoda `db.collection.find()` zwraca kursor dokumentów.
Metody na kursorze sa połączone z zapytaniami i mogą byc używane do wykonywania
akcji na wynikowym zestawi danych np. do sortowania lub limitu danych.

### `cursor.sort()`
Określa kolejność, w jakiej zapytanie zwraca pasujące dokumenty. Musisz zastosować `sort()` do kursora przed pobraniem 
jakichkolwiek dokumentów z bazy danych.

```mongodb-json
db.collections.find({}).sort({ field_name: 1 })
```
* 1 oznacza kolejność rosnącą
* -1 oznacza kolejnosć malejącą
* możemy sortować po kilku polach
* sortowanie wg liter - pierszeństwo mają duże litery a następnie sortuje po małych

### `cursor.limit()`

Użyj metody limit() na kursorze, aby określić maksymalną liczbę dokumentów, które kursor zwróci. 
`limit()` jest analogiczny do instrukcji LIMIT w bazie danych SQL.
Użyj funkcji `limit()`, aby zmaksymalizować wydajność i zapobiec zwracaniu przez MongoDB większej liczby 
wyników niż jest to wymagane do przetworzenia.

```mongodb-json
db.collection.find(<query>).limit(<number>)
```
```mongodb-json
db.myColl.find().sort({_id: 1}).limit(6)
```

# Projekcje

Zwracanie tylko tych pól które nas interesują a nie wszystkich tak jak domyślnie metody zwracają.
Możemy uwzględniać lub wykluczać pola w wynikach, ale nie oba. Wyjątkiem od tej reguły jest pole `_id`.

## Zwraca tylko określone pola i pole `_id`

Projekcja może jawnie zawierać kilka pól poprzez ustawienie `<field>` na 1 w dokumencie projekcji. 
Poniższa operacja zwraca wszystkie dokumenty pasujące do zapytania. 
W zestawie wyników w pasujących dokumentach zwracane są tylko pola item, status i domyślnie `_id`.
```mongodb-json
db.inventory.find( { status: "A" }, { item: 1, status: 1, _id: 0 } )
```

## Zwróć wszystkie pola oprócz wykluczonych

Zamiast wymieniać pola, które mają zostać zwrócone w pasującym dokumencie, 
można użyć projekcji, aby wykluczyć określone pola. 
Poniższy przykład zwraca wszystkie pola z wyjątkiem pól `status` i `instock` w pasujących dokumentach:
```mongodb-json
db.inventory.find( { status: "A" }, { status: 0, instock: 0 } )
```

## Zwracanie określonych pól w osadzonych dokumentach

Można zwrócić określone pola w osadzonym dokumencie. Użyj notacji kropkowej, 
aby odnieść się do osadzonego pola i ustawić wartość 1 w dokumencie projekcji.
```mongodb-json
db.inventory.find(
   { status: "A" },
   { item: 1, status: 1, "size.uom": 1 }
)
```

## Pomijanie określonych pól w osadzonych dokumentach

Można wyłączyć określone pola w osadzonym dokumencie. Użyj notacji kropkowej, aby odnieść się do osadzonego pola 
w dokumencie projekcji i ustawić wartość 0.

```mongodb-json
db.inventory.find(
   { status: "A" },
   { "size.uom": 0 }
)
```

# Counting Documents
Zwraca liczbę całkowitą określającą liczbę dokumentów pasujących do zapytania kolekcji lub widoku.

```mongodb-json
db.collection.countDocuments( <query>, <options> )
```

Options:
* limit - Optional. The maximum number of documents to count.
* skip - Optional. The number of documents to skip before counting.
