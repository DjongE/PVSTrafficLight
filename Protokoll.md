# Parallele und Verteilte Systeme 22SS

## Gruppe C1

### Abdulllatif Zanabili, 7014798 

### Dennis Jongebloed, 7010939

### Rainer Pedde, 7014109

### Dennis Seiler, 7011776



## Glossar

#### Kritischer Abschnitt
Kritische Abschnitte sind Bereiche, auf die mindestens zwei konkurrente Threads oder Prozesse zugreifen möchten, um gemeinsame Daten lesend und/oder schreibend zu verarbeiten.

#### Mutual exclusion
Die Mutual exclusion ist auch als wechselseitiger Ausschluss bekannt. Sie dient dazu, das gleichzeitige Zugreifen von mehreren Prozessen auf die selben geteilten Variablen zu unterbinden.

#### Shared memory/geteilte Variable
Shared memory wird genutzt, damit mehrere Objekte ein und denselben Speicher bzw. Variable nutzen können und somit ohne direkte Übergabe Daten überreichen oder gemeinsam nutzen und verändern können. Die Nutzung von shared memory/geteilten Variablen ist Teil eines Kritischen Abschnitts.

## Aufgabe 1: Nebenläufige Programme in Java

In der ersten Aufgabe des PVS Praktikums haben wir ein Ampelsystem für eine Straßenkreuzung in Java implementiert. Jede Ampel wird durch eine Himmelsrichtung (Enumeration CardinalDirection) identifiziert, d.h. es gibt insgesamt vier Ampeln (NORTH, SOUTH, EAST und WEST). 

Zudem werden die Farben der Ampel mit der Enumeration Colour festgelegt (RED, YELLOW, GREEN). Jede Ampel beginnt mit dem Zustand RED und wechselt die Farben in der Reihenfolge: RED --> GREEN --> YELLOW --> RED --> GREEN usw. Alle Ampeln sollen als unabhängige und nebenläufige Threads arbeiten.

Die Klasse TrafficLight erbt von der Klasse Thread, da wir die Funktionen von Threads benötigen.

```java
public class TrafficLight extends Thread
```

## Klasse Intersection(Main)

In der Klasse Intersection befindet sich die main-Methode. Hier werden die 4 Threads der Klasse TrafficLight erstellt und über die Methode start() aktiviert. Zudem wird die Ampelschaltung nach 50 Millisekunden angehalten.

```java
public class Intersection {
    public static void main(String[] args) {
        CardinalDirection startDir = CardinalDirection.NORTH;

        TrafficLight lightEast = new TrafficLight(CardinalDirection.EAST, startDir);
        lightEast.start();

        TrafficLight lightNorth = new TrafficLight(CardinalDirection.NORTH, startDir);
        lightNorth.start();

        TrafficLight lightSouth = new TrafficLight(CardinalDirection.SOUTH, startDir);
        lightSouth.start();

        TrafficLight lightWest = new TrafficLight(CardinalDirection.WEST, startDir);
        lightWest.start();
        
	    try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        lightEast.halt();
      }
   }
}
```



## Klasse TrafficLight

Die Ampeln besitzen eine CardinalDirection (cd) um den Standort der Ampel zu spezifizieren und eine weitere CardinalDirection (dir) um festzulegen, welche Ampel-Himmelsrichtung zuerst schalten darf. Z.B. wenn 'dir' den Wert 'NORTH' besitzt, dürfen die Ampeln der Himmelsrichtungen 'NORTH' und 'SOUTH' zuerst in die Grünphase schalten. Die Variable (dir) wurde hier als shared Variable festgelegt.

```java
 private CardinalDirection cd;
 private static volatile CardinalDirection dir;
```

Die Variable 'color' beschreibt die Farbe der Ampel und ist für jedes Objekt separat, der Default Wert ist RED.

```java
 private Colour color; 
```

'nextColor' dient zum Erkennen, welche Ampelfarbe für die nächste Ampelphase benötigt wird.

```java
 private static volatile Colour nextColor;
```

Um die Ampelschaltung wieder zu stoppen, wurde ein geteilter Boolean 'stopped' erstellt.

```java
 private static volatile boolean stopped = false;
```

'mainReady' und 'oppReady' sind geteilte Boolean, die speichern, ob die zu schaltenen Richtungen die Farbe der aktuellen Ampelphase angenommen haben.

```java
 private static volatile boolean mainReady = false;
 private static volatile boolean oppReady = false;
```

Das lock Object dient für synchronized als einmalige Referenz, sodass nur ein Thread die damit markierten Bereiche nutzen kann.

```java
 private static final Object lock = new Object();
```



### Der Konstruktor

Der Konstruktor besitzt als Übergabeparameter zweimal die Enumeration 'CardinalDirection'. Der Parameter 'cd' ist der Standort der Ampel und 'dir' ist die Ampel-Startrichtung, welche starten darf.

Zudem wird 'color' auf RED gesetzt, die 'nextColor' Variable wird auf GREEN gesetzt.

```java
 public TrafficLight(CardinalDirection cd, CardinalDirection dir) {
    color = Colour.RED;
    nextColor = Colour.GREEN;
    this.cd = cd;
    TrafficLight.dir = dir;
  }
```



### Die Run () Methode
```java
  @Override
  public void run() {

    Reporter.show(cd, color);

    while (!stopped) {
      if (cd == dir || cd == CardinalDirection.opposite(dir)) {
        if (color != nextColor) { // If current colour is not equal to next (expected) colour
          synchronized (lock) { // mutual exclusion (critical area)

            if (cd == dir) {
              mainReady = true;
              color = Colour.next(color); // Switch current traffic light to the next colour
              Reporter.show(cd, color);
            } else if (cd == CardinalDirection.opposite(dir)) {
              oppReady = true;
              color = Colour.next(color); // Switch current traffic light to the next colour
              Reporter.show(cd, color);
            }
          }

          synchronized (lock) { // mutual exclusion (critical area)
            if (mainReady && oppReady) {
              nextColor = Colour.next(nextColor); // Next (expected) traffic light colour
              mainReady = false;
              oppReady = false;
              if (nextColor == Colour.GREEN) { // If expected colour is green, the Axis will switch
                dir = CardinalDirection.next(cd);
              }
            }
          }
        }
      }
    }
  }
```


Die while-Schleife wird solange ausgeführt, bis die Variable auf 'false' gesetzt wird. Wenn dies passiert, ist die Ampelschaltung gestoppt.

```java
while (!stopped) 
```


Diese Abfrage überprüft, welche Ampel-Himmelrichtung geschaltet werden darf. Als Beispiel, wenn 'dir' auf EAST gesetzt ist, dürfen die Ampeln mit der Richtung 'EAST' und die gegenüberliegende Ampel 'WEST' schalten.
```java
if (cd == dir || cd == CardinalDirection.opposite(dir)) 
```

Hier wird überprüft, ob die Farbe der Ampel nicht mit der erwarteten Farbe übereinstimmt, also noch nicht weiter geschaltet wurde.
```java
if (color != nextColor)
```

Der Ausdruck 'synchronized' dient für die Sicherung der Datenkonsistenz, somit kann immer nur ein Thread den kritischen Abschnitt betreten.
Sobald die Variable 'dir' mit der Variable 'cd' übereinstimmt, wird 'mainReady' auf true gesetzt. Falls 'cd' mit der entgegengesetzten Richtung von 'dir' übereinstimmt, sind wir im Fall der gegenüberliegenden Ampel und somit wird 'oppReady' auf true gesetzt.
In der nächsten Zeile wird die Farbe der Ampel auf die nächste Farbe gesetzt. Die Reihenfolge sieht wie folgt aus: RED -> GREEN -> YELLOW -> RED...
Reporter.show dient zur Ausgabe der Ampel-Himmelrichtung und die aktuelle Farbe der Ampel. Beispielausgabe: "NORTH: GREEN"

```java
  synchronized (lock) { // mutual exclusion (critical area)
    if (cd == dir) {
      mainReady = true;
      color = Colour.next(color); // Switch current traffic light to the next colour
      Reporter.show(cd, color);
    } else if (cd == CardinalDirection.opposite(dir)) {
      oppReady = true;
      color = Colour.next(color); // Switch current traffic light to the next colour
      Reporter.show(cd, color);
    }
  }
```


In diesem synchronized Abschnitt wird erst überprüft, ob beide Ampeln einer Himmelsrichtung geschaltet wurden. Ist dies der Fall wird 'nextcolor' auf die nächste Farbe geändert.
Anschließend werden 'mainReady' und 'oppReady' für die nächste Ampelphase auf false gesetzt.
Bei der zweiten Abfrage wird überprüft, ob 'nextColor' erneut den Wert GREEN angenommen hat. Wenn dies der Fall ist, wird die Himmelsrichtung verändert.

```java
  synchronized (lock) { // mutual exclusion (critical area)
    if (mainReady && oppReady) {
      nextColor = Colour.next(nextColor); // Next (expected) traffic light colour
      mainReady = false;
      oppReady = false;
      if (nextColor == Colour.GREEN) { // If expected colour is green, the Axis will switch
        dir = CardinalDirection.next(cd);
      }
    }
  }
```


### Die halt() Methode

Beim Aufruf dieser Methode wird die Variable 'stopped' auf true gesetzt und somit wird die Ampelschaltung gestoppt. 

```java
  public void halt() {
    stopped = true;
  }
```

Hier läuft das Programm wie folgt ab, ausführlichere Ausgaben liegen der .zip Datei bei:

> EAST:	RED
> WEST:	RED
> SOUTH: RED
> NORTH: RED
> SOUTH: GREEN
> NORTH: GREEN
> NORTH: YELLOW
> SOUTH: YELLOW
> NORTH: RED
> SOUTH: RED
> WEST:	GREEN
> EAST:	GREEN
> WEST:	YELLOW
> EAST:	YELLOW
> WEST:	RED
> EAST:	RED
> NORTH: GREEN
> SOUTH: GREEN
> ////-------------------\\\
