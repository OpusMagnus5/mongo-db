# ACID Transactions

Grupa operacji na bazie danych, które zostaną wykonane jako całość lub wcale.
Wszystkie zmiany wprowadzone przez operacje są zgodne z ograniczeniami bazy danych.
Wiele transakcji może odbywać się w tym samym czasie bez wpływu na wynik innej transakcji.
Wszystkie zmiany dokonane przez operacje w transakcji zostaną zachowane bez względu na wszystko.

## Single-document operations

Operacje na pojedynczych dokumentach są już atomowe domyślnie w MongoDb. np updateOne

## Multi-document operations

Operacje na wielu dokumentach nie są z natury atomowe. Wymagają dodatkowych króków aby stały się transakcyjne.
MongoDb blokuje zasoby zaangażowane w transakcję. Ma wpływ na wydajność i opóźnienia.
Dlatego powinny być używane tylko gdy to konieczne.

**Transakcja ma maksymalny czas działania krótszy niż jedna minuta po pierwszym zapisie**

1. Rozpoczynamy sesje
```
db.getMongo().startSession()
```

2. Rozpoczynamy transakcję
```
session.startTransaction()
```

3. Zapisujemy referencje do kolekcji
```
const account = session.getDatabase('< add database name here>').getCollection('<add collection name here>')
```

4. Wykonujemy operacje na kolekcji
5. Zatwierdzamy transakcje
```
session.commitTransaction()
```
Jeśli w trakcie wykonywania operacji chcemy przerwać tansakcję używamy:

```
session.abortTransaction()
```