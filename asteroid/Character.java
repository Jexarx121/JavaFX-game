package asteroid;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public abstract class Character {

    private Polygon character;
    private Point2D movement;

    public Character(Polygon polygon, int x, int y) {
        this.character = polygon;
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);

        this.movement = new Point2D(0, 0);
    }

    public Polygon getCharacter() {
        return character;
    }

    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 5);
    }

    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 5);
    }

    public Point2D getMovement() {
        return movement;
    }

    public void setMovement(Point2D newMovement) {
        this.movement = newMovement;
    }

    public void move() {
        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX());
        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY());

        if (this.character.getTranslateX() < 0) {
            this.character.setTranslateX(this.character.getTranslateX() + Asteroids.WIDTH);
        }

        if (this.character.getTranslateX() > Asteroids.WIDTH) {
            this.character.setTranslateX(this.character.getTranslateX() % Asteroids.WIDTH);
        }

        if (this.character.getTranslateY() < 0) {
            this.character.setTranslateY(this.character.getTranslateY() + Asteroids.HEIGHT);
        }

        if (this.character.getTranslateY() > Asteroids.HEIGHT) {
            this.character.setTranslateY(this.character.getTranslateY() % Asteroids.HEIGHT);
        }
        
    }

    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.character.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.character.getRotate()));

        changeX *= 0.03;
        changeY *= 0.03;

        this.movement = this.movement.add(changeX, changeY);
    }

    public boolean collide(Character other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

}