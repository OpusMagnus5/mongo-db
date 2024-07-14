### CLI

Polecenie zwraca listę często używanych poleceń i krótki opis każdego z nich
```
db.help()
```

Tworzy nowy cluster w Mongo
```
atlas setup --clusterName myAtlasClusterEDU --provider AWS --currentIp --skipSampleData --username myAtlasDBUser --password myatlas-001 | tee atlas_cluster_details.txt
```

Ładowanie przykładowych danych do klastra Atlas\
```
atlas clusters sampleData load myAtlasClusterEDU
```

Wyświtlanie autoryzacji
```
atlas auth login
```

### Struktura
Kolekcja nie musi mieć takich samych dokumentów, schema jest elastyczna\
Database &rarr; Collection &rarr; Document

### Atlas
Rozszerzenie MongoDb - full text search, wizualizacja danych

### Document Data Model

Podstawowym elementem bazy jest dokument.\
Jest on obiektem wyświetlanym w postaci JSON ale zapisywany w formacie BSON.

#### ObjectID
ObjectID to specjalny typ danych aby tworzyć indentyfikator.
Każdy dokument musi posiadać identyfikator (pole _id) jeśli nie jest zapisywane przez nas
baza automtycznie tworzy takie pole i tworzy identyfikator

### Flexible Schema Model
Wspiera poliformiczność. Umożliwia to przechowywanie dokumentów o różnej strukturze 
w tej samej kolekcji.\
Jeśli chcemy dorzucić nowe pole do jakiegoś dokumentu to po prostu to robimy.\
Jest opcjonalna możliwość aby włączyć validacje schemy, która ustawia ograniczenia 
struktury danych.

### Data Modeling

#### Relacje
* One-to-one (pole w dokumencie jak name or title, jeden data set ma jest powiązany z dokładnie jednym data setem)
* One-to-many (lista obiektów w dokumencie, jeden data set może być powiązany z wieloma data setami)
* Many-to-many (wiele data setów jest powiązana z wieloma data setami)

#### Model relationships
* Embedding (dokument zagnieżdżony w dokumencie, używamy gdy ilość danych jest niewielka)
* Referencing (dokument posiada referencje do innego dokumentu w innej kolekcji, używamy gdy ilość powiązanych danych jest duża)

#### Embedding
Jako że zapisujemy relacje jako zagnieżdżone dokumenty unikamy joinów co daje lepszy perfomance dla operacji odczytu.
Pozwala również na update'y w pojedynczym zapytaniu.\
Minusy to, że zagnieżdżone dokumenty powodują, że dokument robi się coraz większy, co powoduje
zwiększenie zapotrzebowania na pamięć i zwiększa czas odczytu, ponieważ dokumenty muszą być
w całości załadowane do pamięci.
Takie duże dokumenty moga przekroczyć dopuszczalny rozmiar dla formatu BSON - 16 MB\
Czas zapisu dużych dokumentów równiez spada ponieważ Mongo musi przepisać cały dokument.\
Trudniejsza implementacja paginacji dla embedded danych.

#### Referencing
Stosujemy gdy chcemy powiązać dokumenty z różnych kolekcji.\
Zapisuje pole _id które jest referencją do dokumentu.\
Dzięki referencjom unikamy duplikacji danych i otrzymujemy mniejsze dokumenty, 
ale zapytanie dla kilku dokumentów zwiększa zapotrzebowanie na zasoby i wpływa
na czas odczytu.

#### Common schema anti-patterns
* duża liczba tablic
* duża liczba kolekcji
* rozrośniete dokumenty
* niepotrzebne indexy
* zapytania bez indeksu
* Dane do których sięgamy razem ale przechowujemy w osobnych kolekcjach
