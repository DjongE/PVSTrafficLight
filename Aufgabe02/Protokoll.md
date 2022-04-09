

# Parallele und Verteilte Systeme 22SS

## Gruppe C1

### Abdulllatif Zanabili, 7014798 

### Dennis Jongebloed, 7010939

### Rainer Pedde, 7014109

### Dennis Seiler, 7011776



## Aufgabe 2: Spezifikationen in mCRL2

### Verkaufsmaschine

```mCRL2
sort
    Coin = struct _5c | _10c | _20c | _50c | Euro;

map
    value: Coin  -> Int;    % the value of a coin as an integer

eqn
	value(_5c) = 5;
	value(_10c) = 10;
	value(_20c) = 20;
	value(_50c) = 50;
	value(Euro) = 100;
sort
    Product = struct tea | coffee | cake | apple;

map
    price: Product  -> Int; % the price of a product as an integer

eqn
    price(tea) = 10;
	price(coffee) = 25;
	price(cake) = 60;
	price(apple) = 80;
	
act
    accept: Coin;        % accept a coin inserted into the machine    
    return: Coin;        % returns change
    offer: Product;      % offer the possibility to order a certain product
    serve: Product;      % serve a certain product
    returnChange: Int;   % request to return the current credit as  change
    
proc
    VendingMachine = VM(0);                                        %Credit fingt mit 0

    VM(credit : Int) =
        sum c:Coin.(credit<200)->accept(c).VM(credit+value(c))     %Credit kleier (200)-> akzeptiert neue Coints
        +sum p:Product.(credit>=price(p)) ->                       %Credit gleich oder größer als Produktpreis
        offer(p).serve(p).VM(credit-price(p))                      %Produktpreis wird von Credit abgezogen
        + (credit>0) -> returnChange(credit).ReturnChange(credit) %Return refund credit
    ;

ReturnChange(credit : Int) =
        (credit>=100) -> return(100).ReturnChange(100) <>
        (credit>=50) -> return(50).ReturnChange(50) <>
        (credit>=20) -> return(20).ReturnChange(20) <>
        (credit>=10) -> return(10).ReturnChange(10) <>
        (credit>=5) -> return(5).ReturnChange(5) <> 
        VM(credit)
    ;

init
        VendingMachine
;
```

Benennen verschiedener Namen für Datentyp(Veriable) `Coin` mit `sort` (Münzwerte).

```mCRL2
sort
    Coin = struct _5c | _10c | _20c | _50c | Euro;
```

definiert mann den Typ der Variablen `Coin` als Integer mit `map` .

> `c -> p` ist main  if(c) {p}

```mCRL2
map
    value: Coin  -> Int;    % the value of a coin as an integer
```

Variablen Werte zuweisen mit `eqn`.

```mCRL2
eqn
	value(_5c) = 5;
	value(_10c) = 10;
	value(_20c) = 20;
	value(_50c) = 50;
	value(Euro) = 100;
```



Benennen verschiedener Namen für Datentyp(Veriable) `Product` mit `sort` (Produktarten).

```mCRL2
sort
    Product = struct tea | coffee | cake | apple;
```

definiert mann den Typ der Variablen `Product` als Integer mit `map` .

```mCRL2
map
    price: Product  -> Int; % the price of a product as an integer
```

Variablen Werte zuweisen mit `eqn` (Produktpreise).

```mCRL2
eqn
    price(tea) = 10;
	price(coffee) = 25;
	price(cake) = 60;
	price(apple) = 80;
```

Benennen verschiedener Aktionen mit  `act`

```mCRL2
act
    accept: Coin;        % accept a coin inserted into the machine    
    return: Coin;        % returns change
    offer: Product;      % offer the possibility to order a certain product
    serve: Product;      % serve a certain product
    returnChange: Int;   % request to return the current credit as  change
```

Programm bearbeiten mit `proc`

1. Der Prozess beginnt bei Null Guthaben in der Maschine.

2. Wenn der Wert des Guthabens weniger als 200 beträgt, wird der Vorgang akzeptiert und in `c` gespeichert, und jede nachfolgende Hinzufügung wird gesammelt und in `c` gespeichert.

3. Wenn der Preis des Produkts größer oder gleich dem eingezahlten Betrag ist, erscheinen die entsprechenden Angebote und der Preis des ausgewählten Produkts wird vom eingezahlten Guthaben abgezogen.

4. Wenn der Rest größer als Null ist, wird der überschüssige Betrag gemäß der Rückgabemethode (ReturnChange) zurückerstattet.

   > `c -> p` ist main  if(c) {p}

```mCRL2
proc
    VendingMachine = VM(0);                                        %Credit fingt mit 0

    VM(credit : Int) =
        sum c:Coin.(credit<200)->accept(c).VM(credit+value(c))     %Credit kleier (200)-> akzeptiert neue Coints
        +sum p:Product.(credit>=price(p)) ->                       %Credit gleich oder größer als Produktpreis
        offer(p).serve(p).VM(credit-price(p))                      %Produktpreis wird von Credit abgezogen
        + (credit>0) -> returnChange(credit).ReturnChange(credit)  %Return refund credit
    ;
```

Es wird vor jedem Rück Vorgang durchgeführt,ob das das Guthaben gleich oder größer als bestimmte Wert ist.

Wenn der für die Rückgabe zugewiesene Betrag größer oder gleich 100 ist, geben Sie 100 zurück, andernfalls, wenn er größer oder gleich 50 ist, geben Sie 50 zurück und so weiter.
Und am Ende, wenn es weniger als 5 ist, wird es in seinem Gleichgewicht bewahrt (Zustand existiert nicht).

> `c -> p <> n` ist main  if(c) {p} if else{n}

```mCRL2
ReturnChange(credit : Int) =
        (credit>=100) -> return(100).ReturnChange(100) <>   %wenn Credit gleich oder größer als Euro
        (credit>=50) -> return(50).ReturnChange(50) <>		%wenn Credit gleich oder größer als 50 Cent
        (credit>=20) -> return(20).ReturnChange(20) <>		%wenn Credit gleich oder größer als 20 Cent
        (credit>=10) -> return(10).ReturnChange(10) <>		%wenn Credit gleich oder größer als 10 Cent
        (credit>=5) -> return(5).ReturnChange(5) <> 		%wenn Credit gleich oder größer als 5 Cent
        VM(credit)											%oder return credit
    ;
```



 VendingMachine Prozess begonnen.

```mCRL2
init
        VendingMachine
    ;
```



### TrafficLights

#### Version01

```mCRL2
sort
    CardinalDirection = struct north | east | south | west;   % 4 directions
    Axis = struct nsAxis | ewAxis;                            % 2 axes
    
map
    axis: CardinalDirection -> Axis;

eqn
    axis(north) = nsAxis;
    axis(south) = nsAxis;
    axis(east) = ewAxis;
    axis(west) = ewAxis;

sort
    Colour =  struct red | yellow | green;                    % 3 colours

map
    next : Colour -> Colour;

eqn
    next(red) = green;
    next(green) = yellow;
    next(yellow) = red;

act
    show : CardinalDirection # Colour;  % the given traffic light shows the given colour

proc
    TrafficLight(d : CardinalDirection, startAxis : Axis) = TrafficLight(d,red);
        
    TrafficLight(d : CardinalDirection, c : Colour) = show(d,c).TrafficLight(d,next(c));

	Intersection = TrafficLight(north, nsAxis) || TrafficLight(east, nsAxis) || TrafficLight(south, nsAxis) || TrafficLight(west, nsAxis);

init
    Intersection
;
```

#### Version02



#### Version03




#### Version04



```java
public class TrafficLight extends Thread //```
```

