package gui.maintenance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.opentest4j.FileInfo;

import domain.machine.MachineDTO;
import domain.maintenance.MaintenanceController;
import domain.maintenance.MaintenanceDTO;
import domain.maintenance.Maintenance;
import domain.user.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.MaintenanceStatus;

public class MaintenanceDetailView extends BorderPane
{
	private MaintenanceController maintenanceController;
	private MaintenanceDTO currentMaintenance;
	private Stage primaryStage;
	private static final String FILE_STORAGE_PATH = "maintenance_files";

	// UI Components
	private Label titleLabel;
	private Label machineInfoLabel;
	private VBox filesSection;
	private FlowPane filesContainer;
	private List<FileInfo> currentFiles;
	private Button editButton;
	private Button completeButton;

	// File types
	private final List<String> supportedFileTypes = Arrays.asList("*.pdf", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.mp4",
			"*.mov", "*.avi");
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	public MaintenanceDetailView(Stage primaryStage, MaintenanceDTO maintenance)
	{
		this.primaryStage = primaryStage;
		this.maintenanceController = new MaintenanceController();

		// Load maintenance data
		if (maintenance != null)
		{
			this.currentMaintenance = maintenance;
		}

		// Create file storage directory if it doesn't exist
		createFileStorageDirectory();

		// Get files from database for this maintenance
		this.currentFiles = getFilesFromDatabase();

		initialize();
	}

	private List<FileInfo> getFilesFromDatabase()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void createFileStorageDirectory()
	{
		try
		{
			// Create maintenance files directory structure
			Path mainPath = Paths.get(FILE_STORAGE_PATH);
			if (!Files.exists(mainPath))
			{
				Files.createDirectory(mainPath);
			}

			// Create directory for this specific maintenance if needed
			if (currentMaintenance != null)
			{
				Path maintenancePath = Paths.get(FILE_STORAGE_PATH, "maintenance_" + currentMaintenance.id());
				if (!Files.exists(maintenancePath))
				{
					Files.createDirectory(maintenancePath);
				}
			}
		} catch (IOException e)
		{
			showAlert(Alert.AlertType.ERROR, "Error", "Could not create file storage directory: " + e.getMessage());
		}
	}

	private void initialize()
	{
		setStyle("-fx-background-color: #f5f5f5;");
		setPadding(new Insets(20, 30, 30, 30));

		// Create top navigation area
		createTopNavigation();

		// Create header with back button and title
		createHeaderSection();

		// Create maintenance info section
		createMaintenanceInfoSection();

		// Create files section (only if files exist)
		if (currentFiles != null && !currentFiles.isEmpty())
		{
			createFilesSection();
		}
	}

	private void createTopNavigation()
	{
		// This would be implemented in a real application
		// Left as a placeholder since it's outside the scope of this view
	}

	private void createHeaderSection()
	{
		// Back button and title
		Button backButton = new Button();
		backButton.setGraphic(new Label("←"));
		backButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
		backButton.setOnAction(e ->
		{
			// Navigate back to maintenance list
			// mainApp.showMaintenanceList();
		});

		titleLabel = new Label("Onderhoud (onderhoudsnummer)");
		titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
		if (currentMaintenance != null)
		{
			titleLabel.setText("Onderhoud #" + currentMaintenance.id());

			// Add machine info if available
			if (currentMaintenance.machine() != null)
			{
				machineInfoLabel = new Label("Machine: " + currentMaintenance.machine().getCode());
				machineInfoLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
			}
		}

		VBox titleVBox = new VBox(5);
		titleVBox.getChildren().add(titleLabel);
		if (machineInfoLabel != null)
		{
			titleVBox.getChildren().add(machineInfoLabel);
		}

		HBox titleBox = new HBox(10);
		titleBox.setAlignment(Pos.CENTER_LEFT);
		titleBox.getChildren().addAll(backButton, titleVBox);

		// Action buttons
		Button reportButton = new Button("Rapport aanmaken");
		reportButton.setStyle(
				"-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 8px 16px;");
		reportButton.setOnAction(e -> generateReport());

		Button uploadButton = new Button("Bestanden toevoegen");
		uploadButton.setStyle(
				"-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 8px 16px;");
		uploadButton.setOnAction(e -> uploadFiles());

		editButton = new Button("Bewerken");
		editButton.setStyle(
				"-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 8px 16px;");
		editButton.setOnAction(e -> editMaintenance());

		completeButton = new Button("Voltooien");
		completeButton.setStyle(
				"-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 4px; -fx-padding: 8px 16px;");
		completeButton.setOnAction(e -> completeMaintenance());

		// Only show complete button if maintenance is not already completed
		if (currentMaintenance != null && currentMaintenance.status() == MaintenanceStatus.COMPLETED)
		{
			completeButton.setDisable(true);
		}

		HBox actionsBox = new HBox(10);
		actionsBox.setAlignment(Pos.CENTER_RIGHT);
		actionsBox.getChildren().addAll(editButton, completeButton, reportButton, uploadButton);

		// Combine title and actions in a header bar
		BorderPane headerPane = new BorderPane();
		headerPane.setLeft(titleBox);
		headerPane.setRight(actionsBox);
		headerPane.setPadding(new Insets(0, 0, 20, 0));

		setTop(headerPane);
	}

	private Object generateReport()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void createMaintenanceInfoSection()
	{
		// Info message
		Label infoLabel = new Label("Hieronder vindt de details van dit onderhoud");
		infoLabel.setStyle(
				"-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-background-radius: 4px; -fx-border-color: #e0e0e0; -fx-border-radius: 4px;");

		// Table for maintenance details
		GridPane table = createMaintenanceTable();

		VBox contentBox = new VBox(15);
		contentBox.getChildren().addAll(infoLabel, table);

		setCenter(contentBox);
	}

	private GridPane createMaintenanceTable()
	{
		GridPane table = new GridPane();
		table.setStyle(
				"-fx-background-color: white; -fx-background-radius: 4px; -fx-border-color: #e0e0e0; -fx-border-radius: 4px;");

		// Column headers
		String[] headers =
		{ "Nr", "Uitvoeringsdatum", "Starttijdstip", "Eindtijdstip", "Naam technicus", "Reden", "Opmerkingen",
				"Bestanden", "Status" };

		// Create header cells
		for (int i = 0; i < headers.length; i++)
		{
			Label headerLabel = new Label(headers[i]);
			headerLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10px;");
			headerLabel.setMaxWidth(Double.MAX_VALUE);
			headerLabel.setAlignment(Pos.CENTER_LEFT);

			table.add(headerLabel, i, 0);
			GridPane.setHgrow(headerLabel, Priority.SOMETIMES);
		}

		// Add separator line
		Region separator = new Region();
		separator.setStyle("-fx-background-color: #e0e0e0; -fx-min-height: 1px; -fx-max-height: 1px;");
		separator.setMaxWidth(Double.MAX_VALUE);
		table.add(separator, 0, 1, headers.length, 1);

		// Add data row if maintenance data exists
		if (currentMaintenance != null)
		{
			// Add data cells for each column
			Label idLabel = new Label(String.valueOf(currentMaintenance.id()));
			idLabel.setStyle("-fx-padding: 10px;");
			table.add(idLabel, 0, 2);

			Label executionDateLabel = new Label(currentMaintenance.executionDate() != null
					? currentMaintenance.executionDate().format(dateFormatter)
					: "");
			executionDateLabel.setStyle("-fx-padding: 10px;");
			table.add(executionDateLabel, 1, 2);

			Label startTimeLabel = new Label(
					currentMaintenance.startDate() != null ? currentMaintenance.startDate().format(timeFormatter) : "");
			startTimeLabel.setStyle("-fx-padding: 10px;");
			table.add(startTimeLabel, 2, 2);

			Label endDateLabel = new Label(
					currentMaintenance.endDate() != null ? currentMaintenance.endDate().format(timeFormatter) : "");
			endDateLabel.setStyle("-fx-padding: 10px;");
			table.add(endDateLabel, 3, 2);

			Label technicianLabel = new Label(
					currentMaintenance.technician() != null ? currentMaintenance.technician().getFirstName() + " "
							+ currentMaintenance.technician().getLastName() : "");
			technicianLabel.setStyle("-fx-padding: 10px;");
			table.add(technicianLabel, 4, 2);

			Label reasonLabel = new Label(currentMaintenance.reason() != null ? currentMaintenance.reason() : "");
			reasonLabel.setStyle("-fx-padding: 10px;");
			table.add(reasonLabel, 5, 2);

			Label commentsLabel = new Label(currentMaintenance.comments() != null ? currentMaintenance.comments() : "");
			commentsLabel.setStyle("-fx-padding: 10px;");
			table.add(commentsLabel, 6, 2);

			Button addFilesBtn = new Button("Bestanden toevoegen");
			addFilesBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 4px;");
			addFilesBtn.setOnAction(e -> uploadFiles());
			HBox fileBox = new HBox(addFilesBtn);
			fileBox.setPadding(new Insets(5));
			table.add(fileBox, 7, 2);

			Label statusLabel = new Label(
					currentMaintenance.status() != null ? currentMaintenance.status().toString() : "");
			statusLabel.setStyle("-fx-padding: 10px;");
			table.add(statusLabel, 8, 2);
		}

		return table;
	}

	private void createFilesSection()
	{
		filesSection = new VBox(20);
		filesSection.setPadding(new Insets(20, 0, 0, 0));

		// Files header
		Label filesHeader = new Label("Bestanden");
		filesHeader.setFont(Font.font("System", FontWeight.BOLD, 18));

		// File container
		filesContainer = new FlowPane();
		filesContainer.setHgap(20);
		filesContainer.setVgap(20);

		// Add files to container
		for (FileInfo file : currentFiles)
		{
			filesContainer.getChildren().add(createFileBox(file));
		}

		filesSection.getChildren().addAll(filesHeader, filesContainer);

		// Add files section to bottom of layout
		BorderPane.setMargin(filesSection, new Insets(20, 0, 0, 0));
		setBottom(filesSection);
	}

	private VBox createFileBox(FileInfo fileInfo)
	{
		VBox fileBox = new VBox();
		fileBox.setPrefWidth(217);

		// File preview/thumbnail area
		String fileType = getFileType(fileInfo.name);

		// Create appropriate preview based on file type
		if (fileType.equals("pdf"))
		{
			// Create PDF preview
			VBox pdfPreview = new VBox();
			pdfPreview.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
			pdfPreview.setPrefSize(217, 280);
			pdfPreview.setAlignment(Pos.CENTER);

			// Add PDF icon
			ImageView pdfIcon = new ImageView(); // In a real app, load a PDF icon
			pdfIcon.setFitHeight(100);
			pdfIcon.setPreserveRatio(true);

			Label pdfName = new Label(fileInfo.name);
			pdfName.setStyle("-fx-font-size: 14px;");
			pdfPreview.getChildren().addAll(pdfIcon, pdfName);

			fileBox.getChildren().add(pdfPreview);
		} else if (fileType.equals("image"))
		{
			// For image files, try to load the actual image if available
			Region imagePlaceholder = new Region();
			imagePlaceholder.setPrefSize(217, 160);
			imagePlaceholder.setStyle("-fx-background-color: #e0e0e0;");

			// In a real app, we would load the image from the file path
			if (fileInfo.path != null && !fileInfo.path.isEmpty())
			{
				try
				{
					// Attempt to load the image if it exists
					File imageFile = new File(fileInfo.path);
					if (imageFile.exists())
					{
						Image image = new Image(new FileInputStream(imageFile));
						ImageView imageView = new ImageView(image);
						imageView.setFitWidth(217);
						imageView.setFitHeight(160);
						imageView.setPreserveRatio(true);
						fileBox.getChildren().add(imageView);
					} else
					{
						fileBox.getChildren().add(imagePlaceholder);
					}
				} catch (Exception e)
				{
					fileBox.getChildren().add(imagePlaceholder);
				}
			} else
			{
				fileBox.getChildren().add(imagePlaceholder);
			}
		} else
		{
			// Generic file preview
			VBox genericPreview = new VBox();
			genericPreview.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
			genericPreview.setPrefSize(217, 160);
			genericPreview.setAlignment(Pos.CENTER);

			Label fileName = new Label(fileInfo.name);
			fileName.setStyle("-fx-font-size: 14px;");
			genericPreview.getChildren().add(fileName);

			fileBox.getChildren().add(genericPreview);
		}

		// File info area (file name + download button)
		HBox filebox = new HBox();
		filebox.setStyle("-fx-background-color: #f44336; -fx-padding: 8px;");
		filebox.setPrefWidth(217);

		Label fileName = new Label(fileInfo.name);
		fileName.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
		fileName.setMaxWidth(170);
		HBox.setHgrow(fileName, Priority.ALWAYS);

		Button downloadBtn = new Button();
		downloadBtn.setGraphic(new Label("↓"));
		downloadBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		downloadBtn.setOnAction(e -> downloadFile(fileInfo));

		Button deleteBtn = new Button();
		deleteBtn.setGraphic(new Label("✕"));
		deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		deleteBtn.setOnAction(e -> deleteFile(fileInfo));

		filebox.getChildren().addAll(fileName, downloadBtn, deleteBtn);

		fileBox.getChildren().add(filebox);

		return fileBox;
	}

	private String getFileType(String fileName)
	{
		String lowerCaseName = fileName.toLowerCase();
		if (lowerCaseName.endsWith(".pdf"))
		{
			return "pdf";
		} else if (lowerCaseName.matches(".*\\.(jpg|jpeg|png|gif)$"))
		{
			return "image";
		} else if (lowerCaseName.matches(".*\\.(mp4|mov|avi)$"))
		{
			return "video";
		}
		return "other";
	}

	private void uploadFiles()
	{
		if (currentMaintenance == null)
		{
			showAlert(Alert.AlertType.WARNING, "Upload Error",
					"Er is geen onderhoud geselecteerd om bestanden aan toe te voegen.");
			return;
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Upload Files");

		// Set supported file types
		FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");
		FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg",
				"*.png", "*.gif");
		FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mov",
				"*.avi");

		fileChooser.getExtensionFilters().addAll(pdfFilter, imageFilter, videoFilter);

		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

		if (selectedFiles != null && !selectedFiles.isEmpty())
		{
			// If files section doesn't exist yet, create it
			if (filesSection == null)
			{
				// Initialize files list if needed
				if (currentFiles == null)
				{
					currentFiles = new ArrayList<>();
				}

				// Create the files section
				createFilesSection();
			}

			// Create target directory for this maintenance if it doesn't exist
			String maintenanceDir = FILE_STORAGE_PATH + "/maintenance_" + currentMaintenance.id();
			File directory = new File(maintenanceDir);
			if (!directory.exists())
			{
				directory.mkdirs();
			}

			// Add each selected file
			for (File file : selectedFiles)
			{
				try
				{
					// Copy file to maintenance directory
					String targetPath = maintenanceDir + "/" + file.getName();
					File targetFile = new File(targetPath);

					// Copy the file
					Files.copy(file.toPath(), targetFile.toPath());

					// Add to UI and list
					FileInfo newFile = new FileInfo(file.getName(), null, targetPath);
					currentFiles.add(newFile);
					filesContainer.getChildren().add(createFileBox(newFile));

					// Here we would add to database in a real application
					// saveFileToDatabase(newFile, currentMaintenance.id());

				} catch (IOException e)
				{
					showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to upload file: " + e.getMessage());
				}
			}
		}
	}

	private void downloadFile(FileInfo fileInfo)
	{
		if (fileInfo.path == null || fileInfo.path.isEmpty())
		{
			showAlert(Alert.AlertType.WARNING, "Download Error", "Bestand pad niet beschikbaar.");
			return;
		}

		File sourceFile = new File(fileInfo.path);
		if (!sourceFile.exists())
		{
			showAlert(Alert.AlertType.WARNING, "Download Error", "Bestand niet gevonden: " + fileInfo.name);
			return;
		}

		// Choose the download location
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Selecteer download locatie");
		File selectedDirectory = directoryChooser.showDialog(primaryStage);

		if (selectedDirectory != null)
		{
			try
			{
				// Create target file
				File targetFile = new File(selectedDirectory.getAbsolutePath() + File.separator + fileInfo.name);

				// Copy the file
				Files.copy(sourceFile.toPath(), targetFile.toPath());

				showAlert(Alert.AlertType.INFORMATION, "Download Succesvol",
						"Bestand is gedownload naar: " + targetFile.getAbsolutePath());

			} catch (IOException e)
			{
				showAlert(Alert.AlertType.ERROR, "Download Error", "Fout tijdens downloaden: " + e.getMessage());
			}
		}
	}

	private void deleteFile(FileInfo fileInfo)
	{
		// Confirm deletion
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Bevestig verwijderen");
		confirmAlert.setHeaderText("Weet u zeker dat u dit bestand wilt verwijderen?");
		confirmAlert.setContentText("Bestand: " + fileInfo.name);

		Optional<ButtonType> result = confirmAlert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK)
		{
			try
			{
				// Delete from filesystem if path exists
				if (fileInfo.path != null && !fileInfo.path.isEmpty())
				{
					File file = new File(fileInfo.path);
					if (file.exists())
					{
						file.delete();
					}
				}

				// Remove from current list and UI
				currentFiles.remove(fileInfo);
				refreshFilesSection();

				// In a real app: Remove from database
				// deleteFileFromDatabase(fileInfo.id);

			} catch (Exception e)
			{
				showAlert(Alert.AlertType.ERROR, "Delete Error", "Fout tijdens verwijderen: " + e.getMessage());
			}
		}
	}

	private void refreshFilesSection()
	{
		// Clear existing files UI
		if (filesSection != null)
		{
			getChildren().remove(filesSection);
		}

		// Recreate files section if there are files
		if (currentFiles != null && !currentFiles.isEmpty())
		{
			createFilesSection();
		}
	}

	private void editMaintenance()
	{
		if (currentMaintenance == null)
		{
			return;
		}

		// Create the custom dialog
		Dialog<MaintenanceDTO> dialog = new Dialog<>();
		dialog.setTitle("Onderhoud Bewerken");
		dialog.setHeaderText("Wijzig onderhoud #" + currentMaintenance.id());

		// Set the button types
		ButtonType saveButtonType = new ButtonType("Opslaan");
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		// Create the form grid
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		// Create form fields with current values
		DatePicker executionDatePicker = new DatePicker(currentMaintenance.executionDate());
		executionDatePicker.setPromptText("Uitvoeringsdatum");

		TextField startTimeField = new TextField();
		if (currentMaintenance.startDate() != null)
		{
			startTimeField.setText(currentMaintenance.startDate().format(timeFormatter));
		}
		startTimeField.setPromptText("Start tijd (HH:MM)");

		TextField endTimeField = new TextField();
		if (currentMaintenance.endDate() != null)
		{
			endTimeField.setText(currentMaintenance.endDate().format(timeFormatter));
		}
		endTimeField.setPromptText("Eind tijd (HH:MM)");

		TextField reasonField = new TextField(currentMaintenance.reason());
		reasonField.setPromptText("Reden");

		TextArea commentsArea = new TextArea(currentMaintenance.comments());
		commentsArea.setPromptText("Opmerkingen");
		commentsArea.setPrefRowCount(3);

		ComboBox<MaintenanceStatus> statusComboBox = new ComboBox<>();
		statusComboBox.getItems().addAll(MaintenanceStatus.values());
		statusComboBox.setValue(currentMaintenance.status());

		// Add labels and fields to grid
		grid.add(new Label("Uitvoeringsdatum:"), 0, 0);
		grid.add(executionDatePicker, 1, 0);
		grid.add(new Label("Start tijd:"), 0, 1);
		grid.add(startTimeField, 1, 1);
		grid.add(new Label("Eind tijd:"), 0, 2);
		grid.add(endTimeField, 1, 2);
		grid.add(new Label("Reden:"), 0, 3);
		grid.add(reasonField, 1, 3);
		grid.add(new Label("Opmerkingen:"), 0, 4);
		grid.add(commentsArea, 1, 4);
		grid.add(new Label("Status:"), 0, 5);
		grid.add(statusComboBox, 1, 5);

		dialog.getDialogPane().setContent(grid);

		// Request focus on the first field
		executionDatePicker.requestFocus();

		// Convert the result to maintenance data when the save button is clicked
		dialog.setResultConverter(dialogButton ->
		{
			if (dialogButton == saveButtonType)
			{
				try
				{
					// Validate and parse input
					LocalDate executionDate = executionDatePicker.getValue();

					// Parse time fields
					LocalDateTime startDateTime = null;
					LocalDateTime endDateTime = null;

					if (executionDate != null && !startTimeField.getText().isEmpty())
					{
						startDateTime = LocalDateTime.of(executionDate, LocalDateTime
								.parse(executionDate + "T" + startTimeField.getText() + ":00").toLocalTime());
					}

					if (executionDate != null && !endTimeField.getText().isEmpty())
					{
						endDateTime = LocalDateTime.of(executionDate, LocalDateTime
								.parse(executionDate + "T" + endTimeField.getText() + ":00").toLocalTime());
					}

					// Validate end time is after start time
					if (startDateTime != null && endDateTime != null && endDateTime.isBefore(startDateTime))
					{
						throw new IllegalStateException("Eindtijd moet na starttijd liggen.");
					}

					// Create updated MaintenanceDTO (using the record constructor)
					return new MaintenanceDTO(currentMaintenance.id(), executionDate, startDateTime, endDateTime,
							currentMaintenance.technician(), reasonField.getText(), commentsArea.getText(),
							statusComboBox.getValue(), currentMaintenance.machine());
				} catch (Exception e)
				{
					showAlert(Alert.AlertType.ERROR, "Invoer fout", e.getMessage());
					return null;
				}
			}
			return null;
		});

		// Show the dialog and process the result
		Optional<MaintenanceDTO> result = dialog.showAndWait();
		result.ifPresent(updatedMaintenance ->
		{
			try
			{
				// Update maintenance in controller/database
				Maintenance maintenance = maintenanceController.getMaintenance(updatedMaintenance.id());
				if (maintenance != null)
				{
					maintenance.setExecutionDate(updatedMaintenance.executionDate());
					maintenance.setStartDate(updatedMaintenance.startDate());
					maintenance.setEndDate(updatedMaintenance.endDate());
					maintenance.setReason(updatedMaintenance.reason());
					maintenance.setComments(updatedMaintenance.comments());
					maintenance.setStatus(updatedMaintenance.status());

					// Update the current maintenance DTO reference
					currentMaintenance = updatedMaintenance;

					// Refresh the view
					getChildren().clear();
					initialize();

					showAlert(Alert.AlertType.INFORMATION, "Success", "Onderhoud succesvol bijgewerkt.");
				}
			} catch (Exception e)
			{
				showAlert(Alert.AlertType.ERROR, "Update fout", "Fout bij bijwerken van onderhoud: " + e.getMessage());
			}
		});
	}

	private void completeMaintenance() {
		if (currentMaintenance == null) {
			return;
		}
		
		// Confirm completion
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Onderhoud voltooien");
		confirmAlert.setHeaderText("Weet u zeker dat u dit onderhoud wilt voltooien?");
		confirmAlert.setContentText("Onderhoud #" + currentMaintenance.id() + " zal worden gemarkeerd als voltooid.");
		
		Optional<ButtonType> result = confirmAlert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				// Get maintenance entity from database
				Maintenance maintenance = maintenanceController.getMaintenance(currentMaintenance.id());
				if (maintenance != null) {
					// Set status to completed
					maintenance.setStatus(MaintenanceStatus.COMPLETED);
					
					// Set end date if not already set
					if (maintenance.getEndDate() == null) {
						maintenance.setEndDate(LocalDateTime.now());
					}
					
					// Update in database via controller
					// In a real app, you would have something like:
					// maintenanceController.updateMaintenance(maintenance);
					
					// Update the current DTO reference
					currentMaintenance = new MaintenanceDTO(
						maintenance.getId(),
						maintenance.getExecutionDate(),
						maintenance.getStartDate(),
						maintenance.getEndDate(),
						maintenance.getTechnician(),
						maintenance.getReason(),
						maintenance.getComments(),
						maintenance.getStatus(),
						currentMaintenance.machine()
					);
					
					// Refresh the view
					getChildren().clear();
					initialize();
					
					// Disable the complete button
					completeButton.setDisable(true);
					
					showAlert(Alert.AlertType.INFORMATION, "Success", "Onderhoud succesvol voltooid.");
				}
			} catch (Exception e) {
				showAlert(Alert.AlertType.ERROR, "Complete Error", "Fout bij het voltooien van onderhoud: " + e.getMessage());
			}
		}
	}