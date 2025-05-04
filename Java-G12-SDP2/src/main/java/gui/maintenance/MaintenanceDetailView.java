package gui.maintenance;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.maintenance.FileInfo;
import domain.maintenance.FileInfoController;
import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import domain.maintenance.MaintenanceDTO;
import gui.MainLayout;
import gui.report.AddReportForm;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MaintenanceDetailView extends BorderPane
{
	private MaintenanceController maintenanceController;
	private FileInfoController fileInfoController;
	private MaintenanceDTO currentMaintenance;
	private Stage primaryStage;
	private static final String FILE_STORAGE_PATH = "maintenance_files";

	// UI Components
	private Label titleLabel;
	private Label machineInfoLabel;
	private VBox filesSection;
	private FlowPane filesContainer;
	private List<FileInfo> currentFiles;

	// File types
	private final List<String> supportedFileTypes = Arrays.asList("*.pdf", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.mp4",
			"*.mov", "*.avi");
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private final MainLayout mainLayout;

	public MaintenanceDetailView(MainLayout mainLayout, MaintenanceDTO maintenance)
	{
		this.mainLayout = mainLayout;
		this.maintenanceController = new MaintenanceController();
		this.fileInfoController = new FileInfoController();

		// Load maintenance data
		if (maintenance != null)
		{
			this.currentMaintenance = maintenance;
		}

		// Create file storage directory if it doesn't exist
		createFileStorageDirectory();

		// Get files from database for this maintenance
		this.currentFiles = getFilesFromDatabase();

		// Load CSS
		getStylesheets().add(getClass().getResource("/css/maintenanceDetails.css").toExternalForm());
		getStyleClass().add("maintenance-details");

		initialize();
	}

	private List<FileInfo> getFilesFromDatabase()
	{
		if (currentMaintenance == null)
		{
			return new ArrayList<>();
		}

		// Get files from controller
		return fileInfoController.getFilesForMaintenance(currentMaintenance.id());
	}

	private void createFileStorageDirectory()
	{
		// No longer needed as files are stored in database
	}

	private void initialize()
	{
		setStyle("-fx-background-color: #f5f5f5;");
		setPadding(new Insets(10, 30, 10, 30));

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
		FontIcon backIcon = new FontIcon("fas-arrow-left");
		backIcon.setIconSize(16);
		backIcon.setIconColor(Color.web("#333333"));
		backButton.setGraphic(backIcon);
		backButton.getStyleClass().add("back-button");
		backButton.setOnAction(e -> {
			// Navigate back to maintenance list
			// mainApp.showMaintenanceList();
		});

		titleLabel = new Label("Onderhoud (onderhoudsnummer)");
		titleLabel.getStyleClass().add("title-label");
		if (currentMaintenance != null)
		{
			titleLabel.setText("Onderhoud #" + currentMaintenance.id());

			// Add machine info if available
			if (currentMaintenance.machine() != null)
			{
				machineInfoLabel = new Label("Machine: " + currentMaintenance.machine().code());
				machineInfoLabel.getStyleClass().add("machine-info-label");
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
		FontIcon reportIcon = new FontIcon("fas-file-alt");
		reportIcon.setIconSize(16);
		reportIcon.setIconColor(Color.WHITE);
		reportButton.setGraphic(reportIcon);
		reportButton.getStyleClass().add("action-button");
		reportButton.setOnAction(e -> {
			goToAddRapport(mainLayout, currentMaintenance);
		});

		Button uploadButton = new Button("Bestanden toevoegen");
		FontIcon uploadIcon = new FontIcon("fas-upload");
		uploadIcon.setIconSize(16);
		uploadIcon.setIconColor(Color.WHITE);
		uploadButton.setGraphic(uploadIcon);
		uploadButton.getStyleClass().add("action-button");
		uploadButton.setOnAction(e -> uploadFiles());

		HBox actionsBox = new HBox(10);
		actionsBox.setAlignment(Pos.CENTER_RIGHT);
		actionsBox.getChildren().addAll(reportButton, uploadButton);

		// Combine title and actions in a header bar
		BorderPane headerPane = new BorderPane();
		headerPane.setLeft(titleBox);
		headerPane.setRight(actionsBox);
		headerPane.setPadding(new Insets(0, 0, 20, 0));

		setTop(headerPane);
	}

	private void goToAddRapport(MainLayout mainLayout, MaintenanceDTO maintenance)
	{
		AddReportForm form = new AddReportForm(mainLayout, maintenance);
		form.getStylesheets().add(getClass().getResource("/css/AddRapport.css").toExternalForm());
	}

	private void createMaintenanceInfoSection()
	{
		// Info message
		Label infoLabel = new Label("Hieronder vindt de details van dit onderhoud");
		infoLabel.setStyle(
				"-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-background-radius: 4px; -fx-border-color: #e0e0e0; -fx-border-radius: 4px;");

		// Table for maintenance details
		GridPane table = createMaintenanceTable();

		VBox contentBox = new VBox(10);
		contentBox.getChildren().addAll(infoLabel, table);

		setCenter(contentBox);
	}

	private GridPane createMaintenanceTable()
	{
		GridPane table = new GridPane();
		table.getStyleClass().add("maintenance-table");

		// Column headers
		String[] headers = { "Onderhoudsnummer", "Uitvoeringsdatum", "Starttijd", "Eindtijd", "Technicus", "Reden",
				"Opmerkingen", "Status" };

		// Create header cells
		for (int i = 0; i < headers.length; i++)
		{
			Label headerLabel = new Label(headers[i]);
			headerLabel.getStyleClass().add("table-header");
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
			idLabel.getStyleClass().add("table-cell");
			table.add(idLabel, 0, 2);

			Label executionDateLabel = new Label(currentMaintenance.executionDate() != null
					? currentMaintenance.executionDate().format(dateFormatter)
					: "");
			executionDateLabel.getStyleClass().add("table-cell");
			table.add(executionDateLabel, 1, 2);

			Label startTimeLabel = new Label(
					currentMaintenance.startDate() != null ? currentMaintenance.startDate().format(timeFormatter) : "");
			startTimeLabel.getStyleClass().add("table-cell");
			table.add(startTimeLabel, 2, 2);

			Label endDateLabel = new Label(
					currentMaintenance.endDate() != null ? currentMaintenance.endDate().format(timeFormatter) : "");
			endDateLabel.getStyleClass().add("table-cell");
			table.add(endDateLabel, 3, 2);

			Label technicianLabel = new Label(
					currentMaintenance.technician() != null ? currentMaintenance.technician().getFirstName() + " "
							+ currentMaintenance.technician().getLastName() : "");
			technicianLabel.getStyleClass().add("table-cell");
			table.add(technicianLabel, 4, 2);

			Label reasonLabel = new Label(currentMaintenance.reason() != null ? currentMaintenance.reason() : "");
			reasonLabel.getStyleClass().add("table-cell");
			table.add(reasonLabel, 5, 2);

			Label commentsLabel = new Label(currentMaintenance.comments() != null ? currentMaintenance.comments() : "");
			commentsLabel.getStyleClass().add("table-cell");
			table.add(commentsLabel, 6, 2);

			Label statusLabel = new Label(
					currentMaintenance.status() != null ? currentMaintenance.status().toString() : "");
			statusLabel.getStyleClass().add("table-cell");
			table.add(statusLabel, 7, 2);
		}

		return table;
	}

	private void createFilesSection()
	{
		filesSection = new VBox(10);
		filesSection.setPadding(new Insets(5, 0, 0, 0));

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

		// Add files section to bottom of layout with minimal top margin
		BorderPane.setMargin(filesSection, new Insets(5, 0, 0, 0));
		setBottom(filesSection);
	}

	private VBox createFileBox(FileInfo fileInfo)
	{
		VBox fileBox = new VBox();
		fileBox.getStyleClass().add("file-box");
		fileBox.setPrefWidth(217);

		// File preview/thumbnail area
		String fileType = fileInfo.getType();
		Node previewArea;
		previewArea = new Region();
		((Region) previewArea).setPrefSize(217, 160);
		((Region) previewArea).getStyleClass().add("file-preview");

		// Create appropriate preview based on file type
		if (fileType.equals("pdf"))
		{
			// PDF preview with icon
			VBox pdfPreview = new VBox(10);
			pdfPreview.setAlignment(Pos.CENTER);
			pdfPreview.setPrefSize(217, 160);

			FontIcon pdfIcon = new FontIcon("fas-file-pdf");
			pdfIcon.setIconSize(60);
			pdfIcon.setIconColor(Color.web("#333333"));

			Label pdfName = new Label(fileInfo.getName());
			pdfName.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
			pdfName.setMaxWidth(200);
			pdfName.setWrapText(true);

			pdfPreview.getChildren().addAll(pdfIcon, pdfName);
			previewArea = pdfPreview;
		} else if (fileType.equals("image"))
		{
			// Image preview
			try
			{
				byte[] imageContent = fileInfoController.getFileContent(fileInfo);
				if (imageContent != null)
				{
					Image image = new Image(new ByteArrayInputStream(imageContent));
					ImageView imageView = new ImageView(image);
					imageView.setFitWidth(217);
					imageView.setFitHeight(160);
					imageView.setPreserveRatio(true);
					imageView.setSmooth(true);
					previewArea = imageView;
				} else
				{
					throw new IOException("No image content available");
				}
			} catch (Exception e)
			{
				// If image loading fails, show placeholder
				VBox imagePreview = new VBox(10);
				imagePreview.setAlignment(Pos.CENTER);
				imagePreview.setPrefSize(217, 160);

				FontIcon imageIcon = new FontIcon("fas-image");
				imageIcon.setIconSize(60);
				imageIcon.setIconColor(Color.web("#333333"));

				Label imageName = new Label(fileInfo.getName());
				imageName.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
				imageName.setMaxWidth(200);
				imageName.setWrapText(true);

				imagePreview.getChildren().addAll(imageIcon, imageName);
				previewArea = imagePreview;
			}
		} else if (fileType.equals("video"))
		{
			// Video preview with icon
			VBox videoPreview = new VBox(10);
			videoPreview.setAlignment(Pos.CENTER);
			videoPreview.setPrefSize(217, 160);

			FontIcon videoIcon = new FontIcon("fas-video");
			videoIcon.setIconSize(60);
			videoIcon.setIconColor(Color.web("#333333"));

			Label videoName = new Label(fileInfo.getName());
			videoName.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
			videoName.setMaxWidth(200);
			videoName.setWrapText(true);

			videoPreview.getChildren().addAll(videoIcon, videoName);
			previewArea = videoPreview;
		} else
		{
			// Generic file preview
			VBox genericPreview = new VBox(10);
			genericPreview.setAlignment(Pos.CENTER);
			genericPreview.setPrefSize(217, 160);

			FontIcon fileIcon = new FontIcon("fas-file");
			fileIcon.setIconSize(60);
			fileIcon.setIconColor(Color.web("#333333"));

			Label fileName = new Label(fileInfo.getName());
			fileName.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
			fileName.setMaxWidth(200);
			fileName.setWrapText(true);

			genericPreview.getChildren().addAll(fileIcon, fileName);
			previewArea = genericPreview;
		}

		// File name and actions area
		HBox fileActions = new HBox();
		fileActions.getStyleClass().add("file-actions");
		fileActions.setPrefWidth(217);
		fileActions.setAlignment(Pos.CENTER_LEFT);

		Label fileName = new Label(fileInfo.getName());
		fileName.getStyleClass().add("file-name");
		fileName.setMaxWidth(170);
		HBox.setHgrow(fileName, Priority.ALWAYS);

		// Download button
		Button downloadBtn = new Button();
		FontIcon downloadIcon = new FontIcon("fas-download");
		downloadIcon.setIconSize(16);
		downloadIcon.setIconColor(Color.WHITE);
		downloadBtn.setGraphic(downloadIcon);
		downloadBtn.setStyle("-fx-background-color: transparent; -fx-padding: 4px;");
		downloadBtn.setOnAction(e -> downloadFile(fileInfo));

		// Delete button
		Button deleteBtn = new Button();
		FontIcon deleteIcon = new FontIcon("fas-trash");
		deleteIcon.setIconSize(16);
		deleteIcon.setIconColor(Color.WHITE);
		deleteBtn.setGraphic(deleteIcon);
		deleteBtn.setStyle("-fx-background-color: transparent; -fx-padding: 4px;");
		deleteBtn.setOnAction(e -> deleteFile(fileInfo));

		// Add buttons to actions area
		fileActions.getChildren().addAll(fileName, downloadBtn, deleteBtn);

		// Add all components to file box
		fileBox.getChildren().addAll(previewArea, fileActions);

		return fileBox;
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

			// Get maintenance entity for database relationship
			Maintenance maintenance = maintenanceController.getMaintenance(currentMaintenance.id());
			if (maintenance == null)
			{
				showAlert(Alert.AlertType.ERROR, "Upload Error", "Could not find maintenance in database.");
				return;
			}

			// Add each selected file
			for (File file : selectedFiles)
			{
				try
				{
					// Create FileInfo entity
					FileInfo newFile = new FileInfo(file.getName(), getFileType(file.getName()), null, maintenance);

					// Save file content to database
					fileInfoController.saveFileContent(file, newFile);

					// Add to UI and list
					currentFiles.add(newFile);
					filesContainer.getChildren().add(createFileBox(newFile));

				} catch (IOException e)
				{
					showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to upload file: " + e.getMessage());
				}
			}
		}
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

	private void downloadFile(FileInfo fileInfo)
	{
		// Choose the download location
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Selecteer download locatie");
		File selectedDirectory = directoryChooser.showDialog(primaryStage);

		if (selectedDirectory != null)
		{
			try
			{
				// Get file content from database
				byte[] content = fileInfoController.getFileContent(fileInfo);
				if (content == null)
				{
					showAlert(Alert.AlertType.WARNING, "Download Error", "Bestand niet beschikbaar.");
					return;
				}

				// Create target file
				File targetFile = new File(selectedDirectory.getAbsolutePath() + File.separator + fileInfo.getName());

				// Write content to file
				try (FileOutputStream fos = new FileOutputStream(targetFile))
				{
					fos.write(content);
				}

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
		confirmAlert.setContentText("Bestand: " + fileInfo.getName());

		Optional<ButtonType> result = confirmAlert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK)
		{
			try
			{
				// Delete from database
				fileInfoController.deleteFile(fileInfo);

				// Remove from current list and UI
				currentFiles.remove(fileInfo);
				refreshFilesSection();

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

	private void showAlert(Alert.AlertType alertType, String title, String content)
	{
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}