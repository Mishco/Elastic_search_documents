package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.People;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Scanner;

public class Main extends Application {
    private static final Logger LOGGER = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private RestHighLevelClient client = new ElasticConnection().getConnectionClient();
    private CreateIndex createIndex = new CreateIndex(client);
    private Scanner sc = new Scanner(System.in);


    public static void main(String[] args) {
        Main main = new Main();
        // main.createConsoleMenu();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getClassLoader().getResource("com.mishco.fxml/main.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("com.mishco.css/application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Elastic Search Document");
            primaryStage.show();
            // Saved to instance
            SavedStage.getInstance().setPrimaryStage(primaryStage);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void createConsoleMenu() {
        while (true) {
            welcomeScreen();
            int val = sc.nextInt();
            switch (val) {
                case 1:
                    enterPerson();
                    break;
                case 2:
                    getPerson();
                    break;
                case 3:
                    //deletePerson();
                    break;
                case 4:
                    getAllPersons();
                    break;
                case 5:
                    sc.close();
                    System.exit(0);
                    return;
                default:
                    LOGGER.error("Bad input");
                    break;
            }
        }
    }

    private void getAllPersons() {
        People people = createIndex.getAllPersons();
        for (int i = 0; i < people.sizeOfpersonList(); i++) {
            LOGGER.info(people.getPersonList().get(i).toString());
        }
    }

    private void getPerson() {
        clearScreen();
        LOGGER.info("Enter name of searching person");
        String inputName = sc.next();
        People people = createIndex.getPersonByName(inputName);
        LOGGER.info("Find this persons: ");
        for (int i = 0; i < people.sizeOfpersonList(); i++) {
            LOGGER.info(people.getPersonList().get(i).toString());
        }

    }

    private void enterPerson() {
        clearScreen();
        LOGGER.info("Enter name of person: ");
        String name = sc.next();
        LOGGER.info("Person inserted --> " + this.createPerson(name));
    }

    private static void handleKeyboard() {

    }

    public static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            // Handle any exceptions.
        }
        System.out.println("-----------------------");
    }

    private static void welcomeScreen() {
        clearScreen();
        System.out.println("Welcome to ElasticSearchDocument CLI");
        System.out.println(" 1 - Enter person");
        System.out.println(" 2 - Get person");
        System.out.println(" 3 - Delete person");
        System.out.println(" 4 - Get all persons");
        System.out.println(" 5 - Exit");
    }

    public Person createPerson(String name) {
        Person p = new Person();
        p.setName(name);
        return createIndex.insertPerson(p);
    }


}
