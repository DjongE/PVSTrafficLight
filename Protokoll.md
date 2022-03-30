# Parallele und Verteilte Systeme 22SS

## Gruppe C1

### Abdulllatif Zanabili, 7014798 

### Dennis Jongebloed, 7010939

### Rainer Pedde, 7014109

### Dennis Seiler, 7011776



## Aufgabe 1: Nebenläufige Programme in Java

In der ersten Aufgabe des PVS Praktikums, haben wir ein Ampelsystem für eine Straßenkreuzung in Java implementiert. Jede Ampel wird durch eine Himmelrichtung (Enumeration CardinalDirection) identifiziert, d.h. gibt es insgesamt vier Ampeln (NORTH, SOUTH, EAST und WEST). 

Zudem werden die Farben der Ampel mit der Enumeration Colour festgelegt (RED, YELLOW, GREEN). Jede Ampel beginnt mit dem Zustand RED und die Reihenfolge der Farben ist: RED --> GREEN --> YELLOW --> RED --> GREEN usw. Alle Ampeln sollen als unabhängige und nebenläufige Threads arbeiten.

Die Klasse TrafficLight erbt von der Klasse Thread, da wir die Funktionen von Threads benötigen

```java
public class TrafficLight extends Thread
```

## Klasse Intersection(Main)

Dies ist die main-Methode, hier werden die 4 Threads der Klasse TrafficLight erstellt und über den Ausdruck start() aktiviert. Zudem wird die Ampelschaltung nach 50 Millisekunden angehalten.

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

Die Ampeln besitzen eine CardinalDirection (cd) um den Standort der Ampel zu spezifizieren und eine weitere CardinalDirection (dir) ist als shared Variable festgelegt. Die Variable 'dir' dient dazu welche Ampel-Himmelsrichtung schalten darf. Z.B., wenn 'dir' den Wert 'NORTH' besitzt, dürfen die Ampeln der Himmelsrichtungen 'NORTH' und 'SOUTH' sich in den kritischen Abschnitt befinden.

```java
private CardinalDirection cd;
private static volatile CardinalDirection dir;
```
Die Variable 'color' beschreibt die Farbe der Ampel und ist für jedes Objekt separat, der Default Wert ist RED.

```java
private Colour color; 
```

Die 'mainColor' besitzt den Wert der Ampel, die sich zuerst in den kritischen Abschnitt befindet. Die 'oppositeColor' ist die Farbe der gegenüberliegenden Ampel und 'nextColor' ist die Farbe, welche die Ampeln als nächstes annehmen sollen. All diese Variablen liegen auf den geteilten Speicher.
```java
private static volatile Colour oppositeColor;
private static volatile Colour nextColor; 
private static volatile Colour mainColor;
```
In der Aufgabe c sollten wir eine Methode definieren, welche die Ampelschaltung stoppt. Dazu haben wir eine weitere geteilte Variable deklariert und definiert.

```java
 private static volatile boolean stopped = false;
```
Das lock Object dient Sicherung der Datenkonsistenz und um den kritischen Bereich "abzusichern".
```java
private static final Object lock = new Object();
```



### Der Konstruktor

Der Konstruktor besitzt als Übergabeparameter zweimal die Enumeration 'CardinalDirection'. Der Parameter 'cd' ist der Standort der Ampel und 'dir' ist die Ampel-Himmelsrichtung, welche starten darf.

Zudem wird 'color' und 'oppositeColor' auf RED gesetzt, die 'nextColor' Variable wird auf GREEN gesetzt.

```java
 public TrafficLight(CardinalDirection cd, CardinalDirection dir) {
    color = Colour.RED;
    mainColor = Colour.RED;
    oppositeColor = Colour.RED;
    nextColor = Colour.GREEN;
    this.cd = cd;
    TrafficLight.dir = dir;
  }
```



### Die Run () Method
```java
 public void run() {

    Reporter.show(cd, color);

    while (!stopped) {
      synchronized (lock) { // mutual exclusion (critical area)
        if (cd == dir || cd == CardinalDirection.opposite(dir)) {

        	if (color != nextColor) { // If current colour is not equal to next (expected) colour
        			    color = Colour.next(color); // Switch current traffic light to the next colour
        			    Reporter.show(cd, color);
        			
        				if (cd == dir) {
        					mainColor = color; // The colour of the current traffic light
        				} else {
        					oppositeColor = color; // The colour of the traffic light opposite
        				}
        	}

        	
        	if (oppositeColor == mainColor) { // opposite color is the same as the main color
        			nextColor = Colour.next(nextColor); // Next (expected) traffic light colour
        			if (nextColor == Colour.GREEN) { // If expected colour is green, the Axis will switch
        			dir = CardinalDirection.next(cd);
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
Der Ausdruck 'synchronizes' dient für die Sicherung der Datenkonsistenz, somit kann immer nur ein Thread den kritischen Abschnitt betreten.
```java
synchronized (lock) 
```
Diese Abfrage überprüft, welche Ampel-Himmelrichtung geschaltet werden darf. Als Beispiel, wenn 'dir' auf EAST gesetzt ist, dürfen die Ampeln mit der Richtung 'EAST' und der gegenüberliegenden Ampel 'WEST' schalten.
```java
if (cd == dir || cd == CardinalDirection.opposite(dir)) 
```

Hier wird überprüft, ob die Farbe der Ampel, nicht mit der erwarteten Farbe übereinstimmt, also noch nicht weiter geschaltet wurde. Wenn dies der Fall ist, darf die Ampel im nächsten Schritt auf die nächste Farbe schalten. Die Reihenfolge der Farben ist wie folgt: RED --> GREEN --> YELLOW --> RED.
```java
if (color != nextColor) {
  color = Colour.next(color);
  Reporter.show(cd, color);
}
```
Wenn die Variable 'dir' mit der Variable 'cd' übereinstimmt, wird 'mainColor' auf 'color' gesetzt. Falls 'cd' nicht 'dir' ist sind wir im Fall der gegenüberliegenden Ampel und somit wird 'oppositeColor' auf 'color' gesetzt.

```java
if (cd == dir) {
  mainColor = color;
} else {
  oppositeColor = color; 
}
```

Sobald die gegenüberliegenden Ampeln die gleiche Farbe besitzen, wird die erwartete Farbe 'nextColor' weiter geschaltet, da 'nextColor' mit dem Wert 'GREEN' startet ist die Reihenfolge wie folgt: GREEN --> YELLOW --> RED --> GREEN...

Bei der zweiten Abfrage wird überprüft, ob 'nextColor' erneut den Wert GREEN angenommen hat. Wenn dies der Fall ist wird die Himmelsrichtung verändert.

```java
if (oppositeColor == mainColor) {
  nextColor = Colour.next(nextColor);
    
  if (nextColor == Colour.GREEN) { 
    dir = CardinalDirection.next(cd);
  }
}
```


### Die halt() Methode

Beim Aufruf dieser Methode wird die Variable 'stopped' auf true gesetzt und somit ist die Ampelschaltung gestoppt. 

```java
  public void halt() {
    stopped = true;
  }
```

Hier läuft das Programm wie folgt ab, ausführlichere Ausgaben liegen der .zip Datei bei:

> EAST:	RED
> WEST:	RED
> SOUTH:	RED
> NORTH:	RED
> SOUTH:	GREEN
> NORTH:	GREEN
> NORTH:	YELLOW
> SOUTH:	YELLOW
> SOUTH:	RED
> NORTH:	RED
> EAST:	GREEN
> WEST:	GREEN
> WEST:	YELLOW
> EAST:	YELLOW
> EAST:	RED
> WEST:	RED
> SOUTH:	GREEN
> NORTH:	GREEN
> NORTH:	YELLOW
> SOUTH:	YELLOW
> SOUTH:	RED
> NORTH:	RED
> EAST:	GREEN
> ////-------------------\\\