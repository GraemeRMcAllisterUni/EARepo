package gppDemos.TSP

class City{

    int x = 0
    int y = 0

    City(x,y){
        this.x = x + 50
        this.y = y + 50
    }

    @Override
    String toString() {
        return " [" + x + ", " + y + "]"
    }
}