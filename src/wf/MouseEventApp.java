/* ....Show License.... */
package wf;
 
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
 
/**
 * A sample that demonstrates various mouse and scroll events and their usage.
 * Click the circles and drag them across the screen. Scroll the whole screen.
 * All events are logged to the console.
 */
public class MouseEventApp extends Application {
 
    private final static int RECT_WIDTH = 310;
    private final static int RECT_HEIGHT = 150;
    private final static int CONSOLE_WIDTH = RECT_WIDTH;
    private final static int CONSOLE_HEIGHT = 80;
    private final static int SMALL_CIRCLE_STARTX = 50;
    private final static int SMALL_CIRCLE_STARTY = 50;
    private final static int BIG_CIRCLE_STARTX = 180;
    private final static int BIG_CIRCLE_STARTY = 50;
    //create a console for logging mouse events
    final ListView<String> console = new ListView<>();
    //create a observableArrayList of logged events that will be listed in console
    final ObservableList<String> consoleObservableList = FXCollections.observableArrayList();
 
    {
        //set up the console
        console.setItems(consoleObservableList);
        console.setLayoutY(RECT_HEIGHT + 5);
        console.setPrefSize(CONSOLE_WIDTH, CONSOLE_HEIGHT);
    }
    //create a rectangle in which our circles can move
    final Rectangle rectangle = new Rectangle(RECT_WIDTH, RECT_HEIGHT, new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
                new Stop(1, Color.rgb(156, 216, 255)),
                new Stop(0, Color.rgb(156, 216, 255, 0.5))
            }));
    //variables for storing initial position before drag of circle
    private double initX;
    private double initY;
    private Point2D dragAnchor;
 
    public Parent createContent() {
        rectangle.setStroke(Color.BLACK);
        // create circle with method listed below: paramethers: name of the circle, color of the circle, radius
        final Circle circleSmall = createCircle("Blue circle", Color.DODGERBLUE, 25);
        circleSmall.setTranslateX(SMALL_CIRCLE_STARTX);
        circleSmall.setTranslateY(SMALL_CIRCLE_STARTY);
        // and a second, bigger circle
        final Circle circleBig = createCircle("Orange circle", Color.CORAL, 40);
        circleBig.setTranslateX(BIG_CIRCLE_STARTX);
        circleBig.setTranslateY(BIG_CIRCLE_STARTY);
        // we can set mouse event to any node, also on the rectangle
        rectangle.setOnMouseMoved((MouseEvent me) -> {
            //log mouse move to console, method listed below
            showOnConsole("Mouse moved, x: " + me.getX() + ", y: " + me.getY());
        });
 
        rectangle.setOnScroll((ScrollEvent event) -> {
            double translateX = event.getDeltaX();
            double translateY = event.getDeltaY();
            // reduce the deltas for the circles to stay in the screen
            for (Circle c : new Circle[]{circleSmall, circleBig}) {
                if (c.getTranslateX() + translateX + c.getRadius() > RECT_WIDTH) {
                    translateX = RECT_WIDTH - c.getTranslateX() - c.getRadius();
                }
                if (c.getTranslateX() + translateX - c.getRadius() < 0) {
                    translateX = -c.getTranslateX() + c.getRadius();
                }
                if (c.getTranslateY() + translateY + c.getRadius() > RECT_HEIGHT) {
                    translateY = RECT_HEIGHT - c.getTranslateY() - c.getRadius();
                }
                if (c.getTranslateY() + translateY - c.getRadius() < 0) {
                    translateY = -c.getTranslateY() + c.getRadius();
                }
            }
            // move the circles
            for (Circle c : new Circle[]{circleSmall, circleBig}) {
                c.setTranslateX(c.getTranslateX() + translateX);
                c.setTranslateY(c.getTranslateY() + translateY);
            }
            // log event
            showOnConsole("Scrolled, deltaX: " + event.getDeltaX()
                    + ", deltaY: " + event.getDeltaY());
        });
        return new Group(rectangle, circleBig, circleSmall, console);
    }
 
    private Circle createCircle(final String name, final Color color, int radius) {
        //create a circle with desired name,  color and radius
        final Circle circle = new Circle(radius, new RadialGradient(0, 0, 0.2, 0.3, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
                    new Stop(0, Color.rgb(250, 250, 255)),
                    new Stop(1, color)
                }));
        //add a shadow effect
        circle.setEffect(new InnerShadow(7, color.darker().darker()));
        //change a cursor when it is over circle
        circle.setCursor(Cursor.HAND);
        //add a mouse listeners
        circle.setOnMouseClicked((MouseEvent me) -> {
            showOnConsole("Clicked on" + name + ", " + me.getClickCount() + "times");
            //the event will be passed only to the circle which is on front
            me.consume();
        });
        circle.setOnMouseDragged((MouseEvent me) -> {
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();
            //calculate new position of the circle
            double newXPosition = initX + dragX;
            double newYPosition = initY + dragY;
            //if new position do not exceeds borders of the rectangle, translate to this position
            if ((newXPosition >= circle.getRadius()) && (newXPosition <= RECT_WIDTH - circle.getRadius())) {
                circle.setTranslateX(newXPosition);
            }
            if ((newYPosition >= circle.getRadius()) && (newYPosition <= RECT_HEIGHT - circle.getRadius())) {
                circle.setTranslateY(newYPosition);
            }
            showOnConsole(name + " was dragged (x:" + dragX + ", y:" + dragY + ")");
        });
        circle.setOnMouseEntered((MouseEvent me) -> {
            //change the z-coordinate of the circle
            circle.toFront();
            showOnConsole("Mouse entered " + name);
        });
        circle.setOnMouseExited((MouseEvent me) -> {
            showOnConsole("Mouse exited " + name);
        });
        circle.setOnMousePressed((MouseEvent me) -> {
            //when mouse is pressed, store initial position
            initX = circle.getTranslateX();
            initY = circle.getTranslateY();
            dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
            showOnConsole("Mouse pressed above " + name);
        });
        circle.setOnMouseReleased((MouseEvent me) -> {
            showOnConsole("Mouse released above " + name);
        });
 
        return circle;
    }
 
    private void showOnConsole(String text) {
        //if there is 8 items in list, delete first log message, shift other logs and  add a new one to end position
        if (consoleObservableList.size() == 8) {
            consoleObservableList.remove(0);
        }
        consoleObservableList.add(text);
    }
 
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }
 
    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}