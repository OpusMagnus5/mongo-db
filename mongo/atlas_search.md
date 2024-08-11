# Atlas Search

Będać zalogowanym w Atlas Search przechodzimy do Database -> Search -> Create Search Index. Możemy tez przez CLI ustawiać indeksy.

## Search index
Definiuje jak powinno odbyć się wyszukiwanie. Jest czymyś innym niż **database index** który przyśpiesza zapytania.

Wykorzystanie search indeksu w aggergacji za pomocą operatora `$search`

```
var pipeline = [
{
  $search: {
    index: "sample_supplies-sales-dynamic",
    text: {
      query: "notepad", // tutaj wpisujemy szukaną wartość
      path: { "wildcard": "*" }
    } } },
{
  $set: {
    score: {
      $meta: "searchScore" }
    }
}
]
db.sales.aggregate(pipeline)
```

### Dynamic mapping
Wszystkie pola są indeksowane opórcz typów boolean, ids i timestamps. To jest domyślne zachowanie.

### Static field mapping
Pola po których szukamy są zawsze takie same. Przydatne gdy dokumenty w kolekcji mają bardzo dużo pól.

#### Definiowanie indeksu przez CLI

```
{
    "name": "sample_supplies-sales-static",
    "searchAnalyzer": "lucene.standard",
    "analyzer": "lucene.standard",
    "collectionName": "sales",
    "database": "sample_supplies",
    "mappings": {
        "dynamic": false,
        "fields": {
            "storeLocation": {
                "type": "string"
            }
        }
    }
}

atlas clusters search indexes create --clusterName myAtlasClusterEDU -f /app/search_index.json
atlas clusters search indexes list --clusterName myAtlasClusterEDU --db sample_supplies --collection sales
```

## Operator `$Serach`

```
{
  $search: {
    "index": "<index-name>", // nazwa indeksu
    "<operator-name>"|"<collector-name>": { // możemy używać dodatkowych operatorów np. $compound
      <operator-specification>|<collector-specification>
    },
    "highlight": {
      <highlight-options>
    },
    "concurrent": true | false,
    "count": {
      <count-options>
    },
    "searchAfter"|"searchBefore": "<encoded-token>",
    "scoreDetails": true| false,
    "sort": {
      <fields-to-sort>: 1 | -1
    },
    "returnStoredSource": true | false,
    "tracking": {
      <tracking-option>
    }
   }
}
```

### Operator `compound`

Operator złożony łączy dwa lub więcej operatorów w jedno zapytanie. 
Każdy element zapytania złożonego nazywany jest klauzulą, a każda klauzula składa się z jednego lub więcej podzapytań.

Operator złożony na etapie agregacji $search pozwala nam nadać wagę różnym polom, a także filtrować nasze wyniki bez 
konieczności tworzenia dodatkowych etapów agregacji. Cztery opcje operatora złożonego to "must", "mustNot", "should" i "filter". \
"must" wykluczy rekordy, które nie spełniają kryteriów. \
"mustNot" wykluczy wyniki, które spełniają kryteria. \
"should" pozwala nadać wagę wynikom spełniającym kryteria, aby pojawiały się jako pierwsze. \
"Filtr" usunie wyniki, które nie spełniają kryteriów.

```
{
  $search: {
    "index": <index name>, // optional, defaults to "default"
    "compound": {
      <must | mustNot | should | filter>: [ { <clauses> } ],
      "score": <options>
    }
  }
}

$search {
  "compound": {
    "must": [{
      "text": {
        "query": "field",
        "path": "habitat"
      }
    }],
    "should": [{
      "range": {
        "gte": 45,
        "path": "wingspan_cm",
        "score": {"constant": {"value": 5}}
      }
    }]
  }
}
```

## $searchMeta and facet

To etap agregacji dla Atlas Search, w którym wyświetlane są metadane związane z wyszukiwaniem. 
Oznacza to, że jeśli nasze wyniki wyszukiwania są podzielone na wiadra za pomocą facetów, możemy to zobaczyć na etapie $searchMeta, 
ponieważ te wiadra zawierają informacje o sposobie formatowania wyników wyszukiwania.

```
{
  "$searchMeta"|"$search": {
    "index": <index name>, // optional, defaults to "default"
    "facet": {
      "operator": {
        <operator-specifications>
      },
      "facets": {
        <facet-definitions>
      }
    }
  }
}
```

```
$searchMeta: {
    "facet": {
        "operator": {
            "text": {
            "query": ["Northern Cardinal"],
            "path": "common_name"
            }
        },
        "facets": {
            "sightingWeekFacet": {
                "type": "date",
                "path": "sighting",
                "boundaries": [ISODate("2022-01-01"), 
                    ISODate("2022-01-08"),
                    ISODate("2022-01-15"),
                    ISODate("2022-01-22")],
                "default" : "other"
            }
        }
    }
}
```