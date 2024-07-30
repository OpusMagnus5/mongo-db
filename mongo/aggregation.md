# Aggregation

Operacje agregacji przetwarzają wiele dokumentów i zwracają obliczone wyniki.\
Operacji agregacji można używać do:
* grupowania wartości z wielu dokumentów
* Wykonywania operacji na zgrupowanych danych w celu zwrócenia pojedynczego wyniku.
* Analizować zmiany danych w czasie.

Aby wykonać operacje agregacji, można użyć:\
* Aggregation pipelines, które są preferowaną metodą wykonywania agregacji.
* Single purpose aggregation methods, które są proste, ale nie mają możliwości potoku agregacji.

```
db.orders.aggregate( [
   // Stage 1: Filter pizza order documents by pizza size
   {
      $match: { size: "medium" }
   },
   // Stage 2: Group remaining documents by pizza name and calculate total quantity
   {
      $group: { _id: "$name", totalQuantity: { $sum: "$quantity" } }
   }
] )
```

## Stages

Jedna z wbudowanych metod, która może zostać wykonana na danych, ale nie zmienia ich na stałe\
Niektóre z operatorów:
* `$group` - grupuje dokumenty na podstawie podanego kryterium\
Dzieli dokumenty na grupy według „klucza grupy”. 
Wynikiem jest jeden dokument dla każdego unikalnego klucza grupy.
```
{
 $group:
   {
     _id: <expression>, // Pole wg którego grupujemy np $city
     <field1>: { <accumulator1> : <expression1> }, // nazwa pola i operator np. $count - totalZips: { $count: { } }
   }
 }
```
* `$match` - filtruje dane które spełniają podane kryterium\
Wykorzystuje operatory z zapytania w metodzie `find()`. Podajemy ten stage jak najwcześniej
aby pipeline mógł użyć indeksów oraz redukuje liczbę dokumentów do dalszej analizy.
```
{ $match: { <query> } }
```
* `$sort` - Sortuje dokumenty wg określonej kolejności
```
{ $sort: { <field1>: <sort order>, <field2>: <sort order> ... } }
-------------------------------------------------------------------
{ $sort : { borough : 1 } } // 1 ascending, -1 descending
```
* `$limit` - Ogranicza liczbę dokumentów przekazywanych do następnego etapu
```
{ $limit: <positive 64-bit integer> }
```
* `$project` - Przekazuje dokumenty z żądanymi polami do następnego stage.
Określone pola mogą być istniejącymi polami z dokumentów wejściowych lub nowo obliczonymi polami.
Ta operacja powinna być zazwyczaj jako ostatnia bo definiuje output.
Domyślnie zawsze zwraca pole `_id` chyba ze wyraźnie zadeklarujemy dla tego pola 0.
```
{ $project: { "<field1>": 0, "<field2>": <expression>, ... } }
// 0 dla ukrycia pola, 1 dla zachowania pola
```
* `$set` - Dodaje nowe pola do dokumentów. Tworzy dokumenty, które zawierają wszystkie istniejące pola z dokumentów wejściowych i nowo dodane pola.
Jeśli nazwa pola jest nazwą już istniejącego pola w dokumencie `$set` nadpisuje jego wartość.
```
{ $set: { <newField>: <expression>, ... } }
```
* `$count` - Dodaje pole do dokumentu które zawiera liczbę wszystkich dokumentów z zapytania
```
{ $count: <string> }
// <string> to nazwa pola wyjściowego, którego wartością jest count. 
// <string> musi być niepustym ciągiem znaków, nie może zaczynać się od $ i nie może zawierać znaku .
```
* `$out` - Pobiera dokumenty zwrócone przez aggregation pipeline i zapisuje je w określonej kolekcji. 
Można określić wyjściową bazę danych. Etap $out musi być ostatnim etapem potoku. Operator $out pozwala strukturze agregacji zwracać zestawy wyników o dowolnym rozmiarze
Jeśli kolekcja określona przez operację `$out` już istnieje, wówczas etap `$out` atomowo zastępuje istniejącą kolekcję nową kolekcją wyników po zakończeniu agregacji.
```
{ $out: "<output-collection>" }
```
```
{ $out: { db: "<output-db>", coll: "<output-collection>" } }
```

Czasami stosuje się znak `$` przy nawie pola aby wstawić wartośc tego pola z dokumentu.
