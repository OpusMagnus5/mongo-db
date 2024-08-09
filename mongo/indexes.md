# Index
Specjalne struktury danych, przechowywują niewielką część danych, 
uporządkowane i łatwe do skutecznego wyszukiwania, są wkaźnikiem identyfikującym dokument.
Przyśpieszają zapytania, redukują operacje I/O na dysku i zapotrzebowanie na zasoby.
Wspierają equality matches, range-based operations i zwrananie posortowanych danych.
Zapytania bez indeksów muszą czytać całą kolekcje dokumentów i sotrtuję dane w pamięci.
Z indeksami zapytania ładują tylko potrzebne dokumenty i dane zwracane są szybciej.\
Domyślnie zawsze jest indeks na polu `_id`. Każde zapytanie powinno używac indeksu.\
Indeksy mają wpływ na koszt operacji zapisu, przy zapisie nowego lub aktualizacji istniejącego
dokumentu struktura danych indeksu. Jeśli mamy zbyt wiele indeksów mogą spowolnić te operacje.

## Typy indeksów
* **Single Field Index** - Indeksy pojedynczych pól zbierają i sortują dane z pojedynczego pola w każdym dokumencie w kolekcji.
* **Compound Index** - Indeksy złożone zbierają i sortują dane z dwóch lub więcej pól w każdym dokumencie w kolekcji. 
Dane są grupowane według pierwszego pola w indeksie, a następnie według każdego kolejnego pola.
* **Multikey Index** - Indeksy typu multikey zbierają i sortują dane przechowywane w tablicach.

### Single Field Index
Podczas tworzenia indeksu określa się: 
* Pole, na którym ma zostać utworzony indeks. 
* Kolejność sortowania indeksowanych wartości (rosnąco lub malejąco). 
  * Kolejność sortowania równa 1 sortuje wartości w kolejności rosnącej. 
  * Kolejność sortowania równa -1 sortuje wartości w kolejności malejącej. 

Aby utworzyć indeks jednopolowy, należy użyć następującego polecenia:

```
db.collection.createIndex( <keys>, <options>, <commitQuorum>)
```
```
db.collection.createIndex(
  {
      "email": 1
  },
  {
      unique: true,
      name: "email_idx",
  }
)
```

### Multikey Indexes
Indeksy typu multikey zbierają i sortują dane z pól zawierających wartości tablicowe. 
Indeksy wieloskładnikowe poprawiają wydajność zapytań dotyczących pól tablicowych. 
Nie trzeba jawnie określać typu indeksu wieloskładnikowego. 
Podczas tworzenia indeksu na polu, które zawiera wartość tablicy, MongoDB automatycznie ustawia ten indeks jako indeks wielopoziomowy. 
MongoDB może tworzyć indeksy wielopoziomowe na tablicach, które przechowują zarówno wartości skalarne (na przykład ciągi i liczby), 
jak i osadzone dokumenty. Jeśli tablica zawiera wiele wystąpień tej samej wartości, indeks zawiera tylko jeden wpis dla tej wartości.\
**Jeśli indeks jest złożony z pól tylko jedna tablica może byc w indeksie.**\
**Może być zarówno ineksem prostym jak i złożonym**

```
db.collection.createIndex( <keys>, <options>, <commitQuorum>)
db.customers.createIndex({ accounts: 1}) - accounts jest tablicą
```

### Compound Indexes
Indeksy złożone zbierają i sortują dane z dwóch lub więcej pól w każdym dokumencie w kolekcji. 
Dane są grupowane według pierwszego pola w indeksie, a następnie według każdego kolejnego pola.

```
db.<collection>.createIndex( {
   <field1>: <sortOrder>,
   <field2>: <sortOrder>,
   ...
   <fieldN>: <sortOrder>
} )
```

#### Index Prefixes
Prefiksy indeksów są początkowymi podzbiorami indeksowanych pól. 
Indeksy złożone obsługują zapytania dotyczące wszystkich pól zawartych w prefiksie indeksu. \
Na przykład, rozważmy ten indeks złożony:

```
{ "item": 1, "location": 1, "stock": 1 }
```
The index has these index prefixes:
* `{ item: 1 }`
* `{ item: 1, location: 1 }`
  
MongoDB może używać indeksu złożonego do obsługi zapytań dotyczących tych kombinacji pól:
* `item`
* `item` and `location`
* `item`, `location` and `stock`

Na początku zawsze powinny być podawane pole do filtrowania zapytań, następnie do 
sortowania i sprawdzania zakresu (np. większe / mniejsze od). Wartość sortowania 0 lub 1 też ma znaczenie.

## Deleting indexes
Usuwamy nieużywane lub zbędne indeksy. Nie możemy usunąć domyślnego indeksu na polu `_id`.
Jeśli nie jesteśmy pewni usunięcia indeksu najpierw możemy go **ukryć**.\
Ukrywa istniejący indeks przed planerem zapytań. 
Indeks ukryty przed planistą zapytań nie jest oceniany w ramach wyboru planu zapytań. 
Ukrywając indeks przed planistą, można ocenić potencjalny wpływ usunięcia indeksu bez faktycznego usuwania indeksu. 
Jeśli wpływ ten jest negatywny, można usunąć ukrycie indeksu zamiast konieczności ponownego tworzenia usuniętego indeksu. 
A ponieważ indeksy są w pełni utrzymywane, gdy są ukryte, indeksy są natychmiast dostępne do użycia po ich usunięciu.
```
db.collection.hideIndex(<index>)
```

Usuwa określony indeks z kolekcji.
```
db.collection.dropIndex(index) // możemy podac nazwę lub sam indeks tak jak przy tworzeniu
```
Jeśli nic nie podamy usuwa z kolekcji wszystkie indeksy oprócz indeksu `_id`. \
Usuwa określone indeksy z kolekcji. Aby określić wiele indeksów do usunięcia, należy przekazać metodzie tablicę nazw indeksów.

```
db.collection.dropIndexes()
db.collection.dropIndexes( [ "a_1_b_1", "a_1", "a_1__id_-1" ] )
```


## Common information
Zwraca wszystkie indeksy w kolekcji
```
db.collection.getIndexes()
```

Zwraca statystyki dotyczące procesu zapytania, w tym używany indeks, liczbę zeskanowanych dokumentów i czas przetwarzania zapytania w milisekundach.\
Ten plan zawiera szczegółowe informacje na temat etapów wykonania (IXSCAN, COLLSCAN, FETCH, SORT itp.).
* Etap IXSCAN wskazuje, że zapytanie korzysta z indeksu i jaki indeks jest wybierany. 
* Etap COLLSCAN wskazuje, że wykonywane jest skanowanie kolekcji, bez użycia indeksów. 
* Etap FETCH wskazuje, że dokumenty są odczytywane z kolekcji. 
* Etap SORT wskazuje, że dokumenty są sortowane w pamięci.
* Etap PROJECTION_COVERED - Wszystkie potrzebne informacje są zwracane przez indeks, nie ma potrzeby pobierania ich z pamięci.
```
db.collection.explain() 
```