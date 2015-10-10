#Guida all'utilizzo degli script

In questa cartella sono contenuti diversi script bash per l'esecuzione dei task utili al calcolo delle metriche.


###Recupero delle domande raw
I file `query_raw_questions.sh` e `query_raw_questions.sql` contengono rispettivamente lo script bash e la query sql per creare un file csv di domande "raw"a partire dal database del dump di stackoverflow.

**Nota:** 

è necessario cambiare nel file `query_raw_questions.sh` il valore della variabile `TEMP_FILE` in modo da indicare il file temporaneo in cui verrà creato il file csv esportato dalla query e allo stesso modo modificare il nome del file temporaneo nello script sql `query_raw_questions.sql`:

`INTO OUTFILE 'percorso_del_file_temporaneo'`

Il percorso deve essere lo stesso in entrambi gli script.

Inoltre è necessario modificare il nome del database nel file `query_raw_questions.sh` alla riga:

`{ time mysql -D NOME_DEL_DATABASE ...`

Per avviare lo script è sufficiente, trovandosi all'interno della cartella degli script, lanciare il comando:


```bash
./query_raw_questions
```

In questo modo verranno generato nella cartella `datasets` un file csv chiamato `raw_questions_<TIMESTAMP>.csv`, dove <TIMESTAMP> e il timestamp dell'esecuzione del comando, che rappresenta tutte le domande del dump con tutti gli attributi necessari al calcolo delle metriche ai fini della sperimentazione. Inoltre verranno generati nella cartella `logs` i file `log_<TIMESTAMP>.log` ed  `errors_<TIMESTAMP>.log` contenenti rispettivamente il log del tempo impiegato per la creazione del file delle domande raw e gli eventuali errori avvenuti durante il processo.

###Filtraggio delle domande valide
Dopo la creazione del file csv con le domande "raw" potrebbe essere necessario filtrare  le domande in base ad una lista conosciuta di domande valide.

A questo scopo è possibile utilizzare lo script `filter_question_ids.sh` ed eseguirlo:


```bash
./filter_question_ids <PATH_FILE_DOMANDE_VALIDE> <PATH_FILE_DOMANDE_RAW> <PATH_FILE_OUTPUT>
```

I parametri sono i seguenti:

`<PATH_FILE_DOMANDE_VALIDE>` è il percorso completo ad un file csv contenente la lista di domande valide per il calcolo delle metriche. Il file deve essere un file csv valido con un header e con i campi separati dal carattere `;`. Inoltre il file deve contenere almeno i 2 campi `PostId` e `IsTheSameTopicBTitle` (usato solo temporaneamente) rappresentanti rispettivamente l'id della domanda di stackoverflow e la concordanza di topic tra titolo e testo della domanda sotto forma della stringa `yes` oppure `no`.

`<PATH_FILE_DOMANDE_RAW>` è il percorso completo al file csv contenente la lista di tutte le domande raw tirate fuori direttamente dal database.

`<PATH_FILE_OUTPUT>` è il percorso completo al file csv risultante che contiene le domande raw filtrate.

Il risultato dell'esecuzione dello script è il file csv generato al percorso `<PATH_FILE_OUTPUT>` oltre ad un file nella cartella `logs` chiamato `filter_valid_questions_<TIMESTAMP>.log` contenente il log dei tempi di esecuzione dello script.

###Calcolo delle metriche in maniera sequenziale
Una volta ottenuto il file csv contenente le domande "raw" che si intende utilizzare per il calcolo delle metriche è possibile lanciare lo script di esecuzione del calcolo in maniera sequenziale tramite il comando:

```bash
./run_metrics_sequential <PATH_FILE_DOMANDE_RAW> <PATH_FILE_OUTPUT>
```
Dove:

`<PATH_FILE_DOMANDE_RAW>` è il percorso completo al file csv contenente la lista delle domande raw per la sperimentazione.

`<PATH_FILE_OUTPUT>` è il percorso completo al file csv risultante che contiene il risultato del calcolo delle metriche.

Il risultato dell'esecuzione dello script è il file csv generato al percorso `<PATH_FILE_OUTPUT>` oltre ad un file nella cartella `logs` chiamato `sequential_metrics_<TIMESTAMP>.log` contenente il log dei tempi di esecuzione dello script.

###Calcolo delle metriche con l'actor system
Una volta ottenuto il file csv contenente le domande "raw" che si intende utilizzare per il calcolo delle metriche è possibile lanciare lo script di esecuzione del calcolo in maniera parallela con l'actor system tramite il comando:


```bash
./run_metrics_with_actors <PATH_FILE_DOMANDE_RAW> <PATH_FILE_OUTPUT> <NUMERO_ATTORI> [-weka]
```

Dove: 

`<PATH_FILE_DOMANDE_RAW>` è il percorso completo al file csv contenente la lista delle domande raw per la sperimentazione.

`<PATH_FILE_OUTPUT>` è il percorso completo al file csv risultante che contiene il risultato del calcolo delle metriche.

`<NUMERO_ATTORI>` è il numero di attori Worker che si intende utilizzare per il calcolo delle metriche.

Infine è possibile aggiungere il flag opzionale `-weka` per fare in modo che l'output csv sia un file nel formato richiesto da `weka`; omettendolo l'output sarà nel formato richiesto da `R`.

Il risultato dell'esecuzione dello script è il file csv generato al percorso `<PATH_FILE_OUTPUT>` oltre ad un file nella cartella `logs` chiamato `metrics_with_<NUMERO_ATTORI>_actors_log_<TIMESTAMP>.log` contenente il log dei tempi di esecuzione dello script.


###Test calcolo con un diverso numero di attori
E' possibile, in fase di test, lanciare uno script per effettuare il test di calcolo delle metriche più volte con un diverso numero di attori. Lo script in questione è chiamato `test_metrics_with_actors.sh` è può essere avviato tramite il comando: 


```bash
./test_metrics_with_actors <PATH_FILE_DOMANDE_RAW> <PATH_FILE_OUTPUT>
```

Dove:

`<PATH_FILE_DOMANDE_RAW>` è il percorso completo al file csv contenente la lista delle domande raw per la sperimentazione.

`<PATH_FILE_OUTPUT>` è il percorso completo al file csv risultante che contiene il risultato del calcolo delle metriche.

Lo script ricalcola in iterazioni successive le stesse metriche usando ogni volta un numero diverso di attori, più precisamente 1, 2, 4, 8, 16, 32, 64, 128, 256 e 512. Da sperimentazioni precedenti si nota che nei casi di 256 e 512 attori il sistema va in `OutOfMemoryException`.

Lo script inoltre genera nella cartella `logs` un file chiamato `metrics_with_actors_log_<TIMESTAMP>.log` contenente il log dei tempi di esecuzione delle varie iterazioni di calcolo delle metriche.
