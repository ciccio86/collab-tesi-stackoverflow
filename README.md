# collab-tesi-stackoverflow
Repository contenente il materiale per la realizzazione di calcolo di metriche su dump di stack overflow.

## Struttura
Il progetto contiene 2 cartelle principali:

`database` contiene tutti gli script necessari alla esecuzione dei task per il calcolo delle metriche sulle domande di stackoverflow. Per un dettaglio consultare il file [README](https://github.com/ciccio86/collab-tesi-stackoverflow/blob/master/database/README.md) associato.

`stackexchange-metrics-calculator` contiene un progetto [SBT](http://www.scala-sbt.org/) che implementa script, librerie e l'actor system in Scala per il calcolo delle metriche. Molti degli script presenti nella cartella `database` utilizzano gli script definiti in questo progetto.

## Requisiti
L'unico requisito per far funzionare gli script del progetto è quello di avere installato sulla propria macchina [SBT](http://www.scala-sbt.org/): il sistema di building e gestione delle dipendenze usato da [Scala](http://www.scala-lang.org/).

Per l'installazione per il proprio sistema riferirsi alla pagina di [download](http://www.scala-sbt.org/download.html).
