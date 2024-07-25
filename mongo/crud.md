# CRUD

## INSERT

### Insert a Single Document

Inserts a single document into a collection:

```
db.collection.insertOne()
``` 

```mongodb-json
db.inventory.insertOne(
   { item: "canvas", qty: 100, tags: ["cotton"], size: { h: 28, w: 35.5, uom: "cm" } }
)
```

`insertOne()` returns a document that includes the newly inserted document's `_id` field value.

### Insert Multiple Documents

Insert multiple documents into a collection:

```mongodb-json
db.collection.insertMany()
```

Pass an array of documents to the method:
```mongodb-json
db.inventory.insertMany([
   { item: "journal", qty: 25, tags: ["blank", "red"], size: { h: 14, w: 21, uom: "cm" } },
   { item: "mat", qty: 85, tags: ["gray"], size: { h: 27.9, w: 35.5, uom: "cm" } },
   { item: "mousepad", qty: 25, tags: ["gel", "blue"], size: { h: 19, w: 22.85, uom: "cm" } }
])
```

`insertMany()` returns a document that includes the newly inserted documents `_id` field values.

### Insert Behavior
If the collection does not currently exist, insert operations will create the collection.
If the document does not specify an `_id` field, MongoDB adds the `_id` field with an ObjectId value to the new document.

## Query Documents

### Select All Documents in a Collection

To select all documents in the collection, pass an empty document as the query filter parameter to the find method.
```mongodb-json
 db.collection.find( {} )
```

#### Equality Condition

Wystarczy po prostu wpisac nazwe pola i jego wartość:
```mongodb-json
db.inventory.find( { status: "D" } )
```

#### Specify Conditions Using Query Operators

Możemy wykorzystywać [operatory](https://www.mongodb.com/docs/manual/reference/operator/query/#std-label-query-selectors
):

#### PORÓWNAWCZE

* `$eq` Matches values that are equal to a specified value.
* `$gt` Matches values that are greater than a specified value.
* `$gte` Matches values that are greater than or equal to a specified value.
* `$in` Matches any of the values specified in an array.
* `$lt` Matches values that are less than a specified value.
* `$lte` Matches values that are less than or equal to a specified value.
* `$ne` Matches all values that are not equal to a specified value.
* `$nin` Matches none of the values specified in an array.

```mongodb-json
db.inventory.find( { status: { $in: [ "A", "D" ] } } )
```

#### LOGICZNE

* `$and` Joins query clauses with a logical AND returns all documents that match the conditions of both clauses.
* `$not` Inverts the effect of a query expression and returns documents that do not match the query expression.
* `$nor` Joins query clauses with a logical NOR returns all documents that fail to match both clauses.
* `$or` Joins query clauses with a logical OR returns all documents that match the conditions of either clause.

Tutaj jest zastosowany operator `$and` niejawnie:
```mongodb-json
db.inventory.find( { status: "A", qty: { $lt: 30 } } )
```

To samo tylko z jawnym operatorem `$and`:
```mongodb-json
db.inventory.find( { $and: [{ status: "A" }, { qty: { $lt: 30 } }] } )
```

**Jawnie stosujemy `$and` gdy stosujemy wielokrotnie inne operatory, ponieważ obiekt json nie może zawierać dwóch taki samych pól w obiekcie.**
```mongodb-json
db.routes.find({
  $and: [
    { $or: [{ dst_airport: "SEA" }, { src_airport: "SEA" }] },
    { $or: [{ "airline.name": "American Airlines" }, { airplane: 320 }] },
  ]
})
```


#### Array Query Operators

* `$all` Matches arrays that contain all elements specified in the query.
* `$elemMatch` Selects documents if element in the array field matches all the specified `$elemMatch` conditions.
* `$size` Selects documents if the array field is a specified size.

Zwraca wszystkie dokumenty które spełniają warunek i pole dim_cm jest tablicą w dokumencie.
```mongodb-json
db.survey.find(
   { results: { $elemMatch: { product: "xyz", score: { $gte: 8 } } } }
)
```

## UPDATE

### Replace a Document

Podmienia dokument, nie zmieniając jego `_id`. Przy podmianie można pominąć w nowym
dokumencie pole `_id` albo musi być takie samo.
```
db.collection.replaceOne(
   <filter>,
   <replacement>,
   {
      upsert: <boolean>,
      writeConcern: <document>,
      collation: <document>,
      hint: <document|string>
   }
)
```
```mongodb-json
db.inventory.replaceOne(
   { _id: ObjectId("e213r42f423yt5h63f4") },
   { item: "paper", instock: [ { warehouse: "A", qty: 60 }, { warehouse: "B", qty: 40 } ] }
)
```
Ostatnie pole (options) jest Opcjonalne. Filter to argument który rozróżnia dokument
do podmiany. Najlepiej podawać unikalne id, bo jeśli podamy bardziej zwyczajne pole
to po prostu podmienimy pierwszy który znajdzie./
Upsert w opcjach jeśli jest true i nie znajdzie dokumentu z filtra to tworzy nowy.

### Update a Single Document

#### Operatory aktualizujące pola:
* `$set` - Podmienia wartość pola podaną wartością
* `$currentDate` - stawia wartość pola na bieżącą datę, jako datę lub znacznik czasu. 
Domyślnym typem jest Date.
* `$inc` - Zwiększa pole o określoną wartość.
* `$min` - Aktualizuje wartość pola do określonej wartości, jeśli określona wartość
jest mniejsza niż bieżąca wartość pola
* `$max` - Aktualizuje wartość pola do określonej wartości, jeśli określona wartość
jest większa niż bieżąca wartość pola.
* `$mul` - Mnoży wartość pola przez liczbę
* `$rename` - Aktualizuje nazwę pola.
* `$setOnInsert` - Jeśli operacja aktualizacji z upsert: true spowoduje wstawienie 
dokumentu, wówczas $setOnInsert przypisuje określone wartości do pól w dokumencie. 
* Jeśli operacja aktualizacji nie powoduje wstawienia, $setOnInsert nie robi nic.
* `$unset` - Usuwa określone pole.

#### Operatory aktualizujące tablice
* `$push` - Dołącza określoną wartość do tablicy.
* `$each` - Modyfikator jest dostępny do użycia z operatorem `$addToSet` i operatorem 
`$push`. Użyj z operatorem $addToSet, aby dodać wiele wartości do tablicy, jeśli wartości nie istnieją w tablicy.
Użyj z operatorem `$push`, aby dołączyć wiele wartości do tablicy.

Finds the first document that matches the filter and applies the specified update modifications.
```
db.collection.updateOne(
   <filter>,
   <update>,
   {
     upsert: <boolean>,
     writeConcern: <document>,
     collation: <document>,
     arrayFilters: [ <filterdocument1>, ... ],
     hint:  <document|string>,
     let: <document>
   }
)
```
```mongodb-json
db.inventory.updateOne(
   { item: "paper" },
   {
     $set: { "size.uom": "cm", status: "P" },
     $currentDate: { lastModified: true }
   }
)
```

Ostatnie pole (options) jest Opcjonalne. Filter to argument który rozróżnia dokument
do podmiany. Najlepiej podawać unikalne id, bo jeśli podamy bardziej zwyczajne pole
to po prostu podmienimy pierwszy który znajdzie./
Upsert w opcjach jeśli jest true i nie znajdzie dokumentu z filtra to tworzy nowy.

### Update and return a Single Document

Updates a single document based on the filter and sort criteria. Returns the original document by default. 
Returns the updated document if returnNewDocument is set to true or returnDocument is set to after.
```
db.collection.findOneAndUpdate(
    <filter>,
    <update document or aggregation pipeline>,
    {
      writeConcern: <document>,
      projection: <document>,
      sort: <document>,
      maxTimeMS: <number>,
      upsert: <boolean>,
      returnDocument: <string>,
      returnNewDocument: <boolean>,
      collation: <document>,
      arrayFilters: [ <filterdocument1>, ... ]
    }
)
```

```mongodb-json
db.grades.findOneAndUpdate(
   { "name" : "R. Stiles" },
   { $inc: { "points" : 5 } }
)
```

### Update Multiple Documents

Updates all documents that match the specified filter for a collection.
Jeśli update się nie powiedzie część kolekcji zostanie zaktualizowanie, nie posiada
rollbacku. Zmiany będą widoczne po ich wykonaniu co może nie być dobre gdy polegamy na 
transakcjach.
```
db.collection.updateMany(
   <filter>,
   <update>,
   {
     upsert: <boolean>,
     writeConcern: <document>,
     collation: <document>,
     arrayFilters: [ <filterdocument1>, ... ],
     hint:  <document|string>,
     let: <document>
   }
)
```

## Delete Documents

### Delete All Documents

Removes all documents that match the filter from a collection.
```mongodb-json
db.collection.deleteMany(
   <filter>,
   {
      writeConcern: <document>,
      collation: <document>
   }
)
```

### Delete Only One Document

Removes a single document from a collection.
```
db.collection.deleteOne(
    <filter>,
    {
      writeConcern: <document>,
      collation: <document>,
      hint: <document|string>
    }
)
```