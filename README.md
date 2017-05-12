# magenta
Das quelloffene Bildbearbeitungs- und Bildanalyse-Tool ImageJ erlaubt das Einbinden von eige-nen Funktionen in JAVA. Beispiel-Plugins sind vorhanden.
Es soll ein neues Plugin zur Farbraumtransformation RGB-> YUV programmiert werden, welches die Berechnungen V = R-G, U = B-G, Y = G+(U+V)/4 nicht zwangsläufig auf Basis von Werten an der aktuellen Position vornimmt, sondern auch Werte G aus der kausalen Nachbar-schaft zulässt. Welche Position genommen wird, hängt von Erfahrungen aus der Nachbarschaft ab. Ziel ist es, betragsmäßig möglichst kleine Werte für U und V zu erhalten. Als Güte-Kriterium dient die Summe der Entropien H(U)+ H(V)+ H(Y). Wichtig ist, dass die RGB-Werte eineindeu-tig wieder aus den YUV-Werten zurück gewonnen werden können: G = Y -(U+V)/4, B = U+G, R = V+G.
Das Plugin ist an verschiedenen Testbildern (Graustufen und Farbbilder) zu testen und die Kom-pressionsergebnisse sind zu dokumentieren.
Alle Untersuchungen sind schriftlich zu dokumentieren. Der Quellcode ist ausführlich zu kom-mentieren. Neben der schriftlichen Arbeit sind alle Quellen (Programmcode, Texte, Testbilder) und Tools abzugeben, damit eine Reproduktion der Ergebnisse möglich ist.

Beispiel Plugins:
https://github.com/imagej/tutorials

#Mail vom 05.05.2017

> Sehr geehrter Herr Prof. Strutz,
>
> wir hängen leider immer noch fest. Wir verstehen die Hintransformation und auch Ihr Entropie-Beispiel.
>
> Wenn man bei der Hintransformation nun einen Wert  Gn/Gm aus der Nachbarschaft nimmt (oder einen Mittelwert bildet), woher weiß man bei der (von der Hintransformation ja unabhängigen) Rücktransformation, zu welchem Pixel der Wert Gn/Gm gehört oder ob sogar der "eigentliche" Pixel ausgewählt werden muss?

Drehen Sie die Sache einfach um. Versuchen Sie beim Decoder aus den bereits rekonstruierten
Signalwerten (RGB) eine Voraussage zu machen, welcher G-Wert wohl der günstigste ist. Der Encoder
kann dann logischer Weise dieselbe Voraussage treffen.

Die Schwierigkeit ist natürlich abzuschätzen wie groß der nächste R oder B- Wert sein wird, um  V =
R-Gn bzw. U = B-Gm zu minimieren.

>
> Zu Ihrem Beispiel CSCMod:
>
> Bei einer Transformation (siehe Anlage) übernehmen Sie den "r"-Wert als "y" und rechnen danach auch damit weiter. Uns ist noch nicht ganz klar wieso. Außerdem hakt es bei uns noch an der Bezeichnung der Farbräume (im Beispiel "A7_1").

Sie haben einen Code-Auschnitt aus dem Decoder (mit Modulo-Ops) ausgewählt. r, g und b stehen nur
für die Komponenten 0, 1 und 2, welche beim Decoder aber Y, U und V sind. Das ist von der Wahl der
Variablen evtl. nicht ganz optimal. addval2 wurde beim Encoder auf U und V drauf addiert, um die
Zahlen nicht-negativ zu machen. Der Decoder muss das natürlich wieder korrigieren vor der
Rücktransformation.


#Mail vom 27.04.2017

Sehr geehrter Herr Lütje,

das ist in der Tat nicht ganz einfach.

Beispiel:
in der Umgebung des aktuellen Punktes herrscht ein rötlicher Farbton vor. Dann wird vermutlich auch
für die nächste Position gelten
V = R - G >> 0
Es sollte für die aktuelle Berechnung also ein möglichst großer G-Wert gewählt werden.
Kann man aus dem Signalverlauf abschätzen, welcher G-Wert aus der Nachbarschaft größer ist als der
aktuelle G-Wert?

Seien Gn und Gm Werte aus der Nachbarschaft (oder der aktuelle), dann würden die Formeln lauten

V = R-Gn; U = B-Gm; Y = G + (U+V)/4  (Hintransformation)

G = Y - (U+V)/4; R = V+Gn; B = U+Gm; (Rücktransformation, Gn und Gm sind schon bekannt)

Der Decoder muss die Auswahl von Gn und Gm beim Encoder nachvollziehen können.

Kommen Sie damit weiter?

Das angehängte PlugIn könnte hilfreich sein für die Entropieberechnungen. Evtl. müssen Sie es
anpassen/erweitern für den Datentyp, den Sie analysieren wollen.


