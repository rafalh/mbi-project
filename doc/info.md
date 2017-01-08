Temat
-----
6. Implementacja algorytmu do wyznaczania pokrycia odczytami zadanych obszarów genomu

Wymagania ogólne
----------------
* Algorytmy implemetowane są przy użyciu silnika Apache Spark w języku Scala
* Algorytmy odczytują i działają poprawnie na rzeczywistych plikach w podanych formatach
* Implementacja obowiązkowo zawiera testy jednostkowe do wszystkich metod
* Zaimplementowany algorytm nie powinien działać znacząco wolniej od istniejącej implementacji
(oczywiście w przypadku, gdy istnieje)
* Kod znajduje sie w repozytorium na githubie
* Projekt zawiera plik do automatycznego budowania w formacie Maven lub sbt

Info z maila
------------
Chodzi o to, żeby być w stanie wyznaczyć dla zadanego zbioru obszarów pokrycie
w każdym z nich na możliwie szczegółowym poziomie.
Np. dla kazdego obszaru podać informacje o pokryciu minimalnym, maksymalnym,
średnim, medianie i odchyleniu std.

Linki
-----
BAM:
ftp://ftp.1000genomes.ebi.ac.uk/vol1/ftp/phase3/data/HG00096/exome_alignment/
BED:
ftp://ftp.1000genomes.ebi.ac.uk/vol1/ftp/technical/reference/exome_pull_down_targets/20130108.exome.targets.bed

https://github.com/HadoopGenomics/Hadoop-BAM
https://github.com/bigdatagenomics/adam
