Variantengenerator
==================

Einleitung
----------
Das ist ein Test-Projekt zur Validierung der Ideen und zum Test der Algorithmik die wir für
die Generierung von Varianten zusammengetragen haben.

Erklärungen
-----------
Eingangsparameter sind die PR-Familien und Nummern, die den Zusammenbau einschränken, sowie
die Terminschlüssel für den Einsatz/Entfall eines Teiles.
PR-Familien und -Nummern müssen von Hand im Szenario definiert werden. 
Die Zeitscheiben werden aus den Einträgen Einsatz/Entfall aus den Teiledefinitionen generiert. Es wird nicht auf Sinnhaftigkeit der Reihenfolge überprüft. Es wird nicht geprüft, ob der Reihenfolgegraph vollständig zusammenhängend ist, oder ob der Graph in Teile zerfällt die u.U. nicht in der richtigen Reihenfolge untereinander stehen.
Das kann man leicht verhindern, indem im Szenario die Teile in zeitlich sinnvoller Reihenfolge stehen. Älteste Teile oben, neueste Teile unten in der Liste.

Benutzung
-------------
Dem Variantengenerator wurde ein Parser spendiert, mit dem man Szenario-Files einlesen kann. Die Einträge in ein Szenario File sind im Wesentlichen mit Semikolon getrennte Wertepaare. Diese kann man erzeugen indem man Excel-Export als CSV Datei durchführt und ggf. mit einem Texteditor über das CSV geht.
Beispiel-Daten sind in github.com nicht hinterlegt. Bitte schickt mir eine email für Testdaten (Szenario-File).

Varianten-Generierung startet man mit *java.exe VarGenerator.jar szenario.txt* , z.B auf der Console.
Der Einfachheit halber kann auch im Ordner *Binary* die generator.bat verwendet werden. Man kann per Drag und Drop im Explorer das Szenario-File auf die .bat ziehen und die Generierung startet ;-) 




