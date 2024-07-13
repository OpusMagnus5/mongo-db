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