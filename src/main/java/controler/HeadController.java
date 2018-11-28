package controler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import main.CreateIndex;
import main.ElasticConnection;
import model.People;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;

public class HeadController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private RestHighLevelClient client = new ElasticConnection().getConnectionClient();
    private CreateIndex createIndex = new CreateIndex(client);
    private ObservableList<Person> data;


    @FXML
    private TableView<Person> tablePeople;
    @FXML
    private TextField addFirstName;
    @FXML
    private Button addButton;
    @FXML
    private TextField searchField;

    private TableColumn<Person, String> nameCol = new TableColumn<>("Name");
    private TableColumn<Person, String> idCol = new TableColumn<>("Person ID");
    final HBox hb = new HBox();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        IndicesClient indices = client.indices();
        LOGGER.info("Connected to ES");
        initTable();
        initSearch();
    }

    private void initSearch() {
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Person> filteredData = new FilteredList<>(data, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (person.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });
        // 5. Add sorted (and filtered) data to the table.
        tablePeople.setItems(filteredData);
    }

    @FXML
    public void addPerson() {
        if (!addFirstName.getText().isEmpty()) {
            Person newPerson = new Person(addFirstName.getText());
            // Insert data to tableView
            data.add(newPerson);
            // Insert data to ES
            createIndex.insertPerson(newPerson);
            addFirstName.clear();
        }
    }

    @FXML
    public void clickItem(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY)  // Checking secondary click on mouse
        {
            showPopupMenu();
        }
    }

    private void showPopupMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem("Update person");
        item1.setOnAction(event -> LOGGER.info("Select Update person"));
        MenuItem item2 = new MenuItem("Remove person");
        item2.setOnAction(event -> {
            LOGGER.info("Select Remove person");

            if (tablePeople.getSelectionModel().getSelectedItem() != null) {
                Person selectedPerson = tablePeople.getSelectionModel().getSelectedItem();
                data.remove(selectedPerson);
                LOGGER.info("Remove " + selectedPerson);
                createIndex.deletePersonById(selectedPerson.getPersonId());

            }
        });

        contextMenu.getItems().addAll(item1, item2);

        tablePeople.setOnContextMenuRequested(event -> contextMenu.show(tablePeople, event.getScreenX(), event.getScreenY()));
    }


    @FXML
    public void addPersonKey(KeyEvent ev) {
        if (ev.getCode().equals(KeyCode.ENTER)) {
            addPerson();
        }
    }


    private void initTable() {
        LOGGER.info(createIndex.getAllPersons());
        People people = createIndex.getAllPersons();

        nameCol.setMinWidth(100);
        idCol.setMinWidth(150);

        data = FXCollections.observableArrayList(people.getPersonList());

        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("Name")
        );
        idCol.setCellValueFactory(
                new PropertyValueFactory<>("personId")
        );

        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(
                t -> ((Person) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                ).setName(t.getNewValue())
        );
        tablePeople.getColumns().setAll(nameCol, idCol);
        tablePeople.setEditable(true);

        // Add data
        tablePeople.setItems(data);
    }


}
