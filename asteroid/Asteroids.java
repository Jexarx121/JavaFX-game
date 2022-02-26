package asteroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class Asteroids extends Application {

    public static int WIDTH = 300;
    public static int HEIGHT = 200;

    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        Text text = new Text(10, 20, "Points: 0");

        pane.setPrefSize(WIDTH, HEIGHT);

        Ship ship = new Ship(WIDTH/2, HEIGHT/2);
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i=0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid((rnd.nextInt(WIDTH/3)), rnd.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }
        List<Projectile> projectiles = new ArrayList<>();

        pane.getChildren().add(ship.getCharacter());
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));
        pane.getChildren().add(text);

        AtomicInteger points = new AtomicInteger();

        Scene scene = new Scene(pane);
        stage.setTitle("Asteroids");
        stage.setScene(scene);
        stage.show();

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });


        new AnimationTimer() {
            
            @Override
            public void handle(long now) {
                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) && projectiles.size() < 1) {
                    Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                    projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                    projectiles.add(projectile);

                    projectile.accelerate();
                    projectile.setMovement(projectile.getMovement().normalize().multiply(3));

                    pane.getChildren().add(projectile.getCharacter());
                }

                ship.move();
                asteroids.forEach(asteroid -> asteroid.move());
                projectiles.forEach(projectile -> projectile.move());

                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        stop();
                    }
                });

                List<Projectile> projectilesToRemove = projectiles.stream().filter(projectile -> {
                    List<Asteroid> collisions = asteroids.stream()
                                                                .filter(asteroid -> asteroid.collide(projectile))
                                                                .collect(Collectors.toList());
                
                    if(collisions.isEmpty()) {
                        return false;
                    }
                
                    collisions.stream().forEach(collided -> {
                        asteroids.remove(collided);
                        pane.getChildren().remove(collided.getCharacter());
                    });
                
                    return true;
                }).collect(Collectors.toList());
                
                projectilesToRemove.forEach(projectile -> {
                    pane.getChildren().remove(projectile.getCharacter());
                    projectiles.remove(projectile);
                    text.setText("Points: " + points.addAndGet(1000));
                });

                if(Math.random() < 0.005) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if(!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }
            }   
        }.start();

        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(Asteroids.class);
    }
}

