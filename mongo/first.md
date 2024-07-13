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
Jeśli chcemy dorzucić nowe pole do jakiegoś dokumentu to po prostu to robimy.
Jest opcjonalna możliwość aby włączyć validacje schemy, która ustawia ograniczenia 
struktury danych.
