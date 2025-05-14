package gui.maintenance;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.kordamp.ikonli.javafx.FontIcon;

import domain.maintenance.FileInfo;
import domain.maintenance.FileInfoController;
import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import dto.MaintenanceDTO;
import gui.MainLayout;
import gui.customComponents.CustomInformationBox;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MaintenanceDetailView extends BorderPane
{
	private MaintenanceController maintenanceController;
	private FileInfoController fileInfoController;
	private MaintenanceDTO currentMaintenance;
	private Stage primaryStage;

	private Label titleLabel;
	private Label machineInfoLabel;
	private VBox filesSection;
	private FlowPane filesContainer;
	private List<FileInfo> currentFiles;
	private ComboBox<String> fileTypeFilter;
	private ComboBox<String> sortOrder;
	private String currentFilter = "Alle";
	private String currentSort = "Datum (Nieuwste)";
	private Label messageLabel;

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private final MainLayout mainLayout;

	public MaintenanceDetailView(MainLayout mainLayout, MaintenanceDTO maintenance)
	{
		this.mainLayout = mainLayout;
		this.maintenanceController = mainLayout.getServices().getMaintenanceController();
		this.fileInfoController = mainLayout.getServices().getFileInfoController();
		this.primaryStage = (Stage) mainLayout.getMainScene().getWindow();

		// Load maintenance data
		if (maintenance != null)
		{
			this.currentMaintenance = maintenance;
		}

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

	private void initialize()
	{
		this.getStylesheets().add(getClass().getResource("/css/tablePane.css").toExternalForm());

		setStyle("-fx-background-color: #f5f5f5;");
		setPadding(new Insets(10, 30, 10, 30));

		// Create header with back button and title
		createHeaderSection();

		// Create maintenance info section first
		createMaintenanceInfoSection();

		// Create files section if files exist
		if (currentFiles != null && !currentFiles.isEmpty())
		{
			createFilesSection();
		}
	}

	private void createHeaderSection()
	{
		// Back button and title
		Button backButton = new Button();
		FontIcon backIcon = new FontIcon("fas-arrow-left");
		backIcon.setIconSize(20);
		backButton.setGraphic(backIcon);
		backButton.getStyleClass().add("back-button");
		backButton.setOnAction(e ->
		{
			mainLayout.showMaintenanceList(currentMaintenance.machine());
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

		/*
		 * // Action buttons Button reportButton = new Button("Rapport aanmaken");
		 * FontIcon reportIcon = new FontIcon("fas-file-alt");
		 * reportIcon.setIconSize(16); reportIcon.setIconColor(Color.WHITE);
		 * reportButton.setGraphic(reportIcon);
		 * reportButton.getStyleClass().add("action-button"); reportButton.setOnAction(e
		 * -> { goToAddRapport(mainLayout, currentMaintenance); });
		 */

		Button uploadButton = new Button("Bestanden toevoegen");
		FontIcon uploadIcon = new FontIcon("fas-upload");
		uploadIcon.setIconSize(16);
		uploadIcon.setIconColor(Color.WHITE);
		uploadButton.setGraphic(uploadIcon);
		uploadButton.getStyleClass().add("action-button");
		uploadButton.setOnAction(e -> uploadFiles());

		HBox actionsBox = new HBox(10);
		actionsBox.setAlignment(Pos.CENTER_RIGHT);
		actionsBox.getChildren().add(uploadButton);

		// Combine title and actions in a header bar
		BorderPane headerPane = new BorderPane();
		headerPane.setLeft(titleBox);
		headerPane.setRight(actionsBox);
		headerPane.setPadding(new Insets(0, 0, 20, 0));

		setTop(headerPane);
	}

	/*
	 * private void goToAddRapport(MainLayout mainLayout, MaintenanceDTO
	 * maintenance) { AddReportForm form = new AddReportForm(mainLayout,
	 * maintenance);
	 * form.getStylesheets().add(getClass().getResource("/css/AddRapport.css").
	 * toExternalForm()); mainLayout.showAddReport(maintenance); }
	 */

	private void createMaintenanceInfoSection()
	{
		// Info message
		HBox infoBox = new CustomInformationBox("Hieronder vindt u de details van dit onderhoud");
		VBox.setMargin(infoBox, new Insets(20, 0, 5, 0));

		// Create message label
		messageLabel = new Label();
		messageLabel.getStyleClass().add("message-label");
		messageLabel.setWrapText(true);
		messageLabel.setVisible(false);
		messageLabel.setMaxWidth(Double.MAX_VALUE);
		VBox.setMargin(messageLabel, new Insets(0, 0, 5, 0));

		// Table for maintenance details
		GridPane table = createMaintenanceTable();

		VBox contentBox = new VBox(5);
		contentBox.getChildren().addAll(infoBox, messageLabel, table);

		// Always set the maintenance info in the center
		setCenter(contentBox);
	}

	private GridPane createMaintenanceTable()
	{
		GridPane table = new GridPane();
		table.getStyleClass().add("maintenance-table");

		// Column headers
		String[] headers =
		{ "Onderhoudsnummer", "Uitvoeringsdatum", "Starttijd", "Eindtijd", "Technieker", "Reden", "Opmerkingen",
				"Status" };

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

			Label technicianLabel = new Label(currentMaintenance.technician() != null
					? currentMaintenance.technician().firstName() + " " + currentMaintenance.technician().lastName()
					: "");
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
		filesSection.getStyleClass().add("files-section");

		// Files header
		Label filesHeader = new Label("Bestanden");
		filesHeader.getStyleClass().add("files-header");

		// Create filter and sort controls
		HBox controlsBox = createFilterControls();
		controlsBox.getStyleClass().add("filter-controls");

		// File container
		filesContainer = new FlowPane();
		filesContainer.getStyleClass().add("files-container");

		// Add files to container
		refreshFilesDisplay();

		// Wrap FlowPane in a ScrollPane to enable scrolling
		ScrollPane scrollPane = new ScrollPane(filesContainer);
		scrollPane.setFitToWidth(true);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.getStyleClass().add("files-scroll-pane");

		// Make the ScrollPane fill the available space
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		scrollPane.setPrefViewportHeight(Integer.MAX_VALUE);
		scrollPane.setMaxHeight(Double.MAX_VALUE);
		scrollPane.setMinHeight(400); // Minimum height to ensure it's always visible

		// Add components to files section
		filesSection.getChildren().addAll(filesHeader, controlsBox, scrollPane);

		// Create a container for both maintenance info and files
		VBox mainContent = new VBox(20);
		mainContent.getChildren().addAll((VBox) getCenter(), filesSection);

		// Set the combined content as the center
		setCenter(mainContent);
	}

	private HBox createFilterControls()
	{
		HBox controlsBox = new HBox(10);
		controlsBox.setAlignment(Pos.CENTER_LEFT);

		// File type filter
		Label filterLabel = new Label("Filter op type:");
		fileTypeFilter = new ComboBox<>();
		fileTypeFilter.getItems().addAll("Alle", "PDF", "Foto");
		fileTypeFilter.setValue(currentFilter);
		fileTypeFilter.setOnAction(e ->
		{
			currentFilter = fileTypeFilter.getValue();
			refreshFilesDisplay();
		});

		// Sort order
		Label sortLabel = new Label("Sorteer op:");
		sortOrder = new ComboBox<>();
		sortOrder.getItems().addAll("Datum (Nieuwste)", "Datum (Oudste)", "Grootte (Grootste)", "Grootte (Kleinste)",
				"Naam (A-Z)", "Naam (Z-A)");
		sortOrder.setValue(currentSort);
		sortOrder.setOnAction(e ->
		{
			currentSort = sortOrder.getValue();
			refreshFilesDisplay();
		});

		controlsBox.getChildren().addAll(filterLabel, fileTypeFilter, sortLabel, sortOrder);
		return controlsBox;
	}

	private void refreshFilesDisplay()
	{
		filesContainer.getChildren().clear();

		// Filter files
		List<FileInfo> filteredFiles = currentFiles.stream().filter(file ->
		{
			if (currentFilter.equals("Alle"))
				return true;
			String fileType = file.getType().toLowerCase();
			switch (currentFilter)
			{
			case "PDF":
				return fileType.equals("pdf");
			case "Foto":
				return fileType.equals("image");
			case "Video":
				return fileType.equals("video");
			default:
				return true;
			}
		}).collect(Collectors.toList());

		// Sort files
		filteredFiles.sort((f1, f2) ->
		{
			switch (currentSort)
			{
			case "Datum (Nieuwste)":
				return f2.getUploadDate().compareTo(f1.getUploadDate());
			case "Datum (Oudste)":
				return f1.getUploadDate().compareTo(f2.getUploadDate());
			case "Grootte (Grootste)":
				return Long.compare(f2.getSize(), f1.getSize());
			case "Grootte (Kleinste)":
				return Long.compare(f1.getSize(), f2.getSize());
			case "Naam (A-Z)":
				return f1.getName().compareToIgnoreCase(f2.getName());
			case "Naam (Z-A)":
				return f2.getName().compareToIgnoreCase(f1.getName());
			default:
				return 0;
			}
		});

		// Add sorted and filtered files to container
		for (FileInfo file : filteredFiles)
		{
			filesContainer.getChildren().add(createFileBox(file));
		}
	}

	private void refreshFilesSection()
	{
		refreshFilesDisplay();

		// If no files left, remove the entire files section
		if (currentFiles.isEmpty())
		{
			VBox mainContent = (VBox) getCenter();
			mainContent.getChildren().remove(filesSection);
			filesSection = null;
		}
	}

	private VBox createFileBox(FileInfo fileInfo)
	{
		VBox fileBox = new VBox();
		fileBox.getStyleClass().add("file-box");

		StackPane previewContainer = new StackPane();
		previewContainer.getStyleClass().add("image-container");

		String fileType = fileInfo.getType();

		if (fileType.equals("pdf"))
		{
			try
			{
				byte[] pdfContent = fileInfoController.getFileContent(fileInfo);
				if (pdfContent != null)
				{
					// Load the first page of the PDF
					PDDocument document = Loader.loadPDF(pdfContent);
					PDFRenderer pdfRenderer = new PDFRenderer(document);

					// Get the first page
					BufferedImage image = pdfRenderer.renderImageWithDPI(0, 150); // Increased DPI for better quality

					// Convert to JavaFX Image
					Image pdfImage = SwingFXUtils.toFXImage(image, null);
					ImageView pdfImageView = new ImageView(pdfImage);

					// Create a container to center the image
					StackPane imageContainer = new StackPane();
					imageContainer.setStyle("-fx-background-color: white;");
					imageContainer.setPrefSize(217, 160);

					// Set dimensions while preserving aspect ratio
					pdfImageView.setFitWidth(200); // Slightly smaller than container to allow for padding
					pdfImageView.setFitHeight(140);
					pdfImageView.setPreserveRatio(true);
					pdfImageView.setSmooth(true);

					// Center the image in the container
					StackPane.setAlignment(pdfImageView, Pos.CENTER);

					// Add the image to the container
					imageContainer.getChildren().add(pdfImageView);

					// Add the container to the preview
					previewContainer.getChildren().add(imageContainer);

					// Clean up
					document.close();
				} else
				{
					throw new IOException("No PDF content available");
				}
			} catch (Exception e)
			{
				// Fallback to icon if preview fails
				VBox pdfPreview = new VBox(10);
				pdfPreview.setAlignment(Pos.CENTER);

				FontIcon pdfIcon = new FontIcon("fas-file-pdf");
				pdfIcon.setIconSize(60);
				pdfIcon.setIconColor(Color.web("#333333"));

				Label pdfName = new Label(fileInfo.getName());
				pdfName.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
				pdfName.setMaxWidth(200);
				pdfName.setWrapText(true);

				pdfPreview.getChildren().addAll(pdfIcon, pdfName);
				previewContainer.getChildren().add(pdfPreview);
			}
		} /*
			 * else if (fileType.equals("video")) { try { byte[] videoContent =
			 * fileInfoController.getFileContent(fileInfo); if (videoContent != null) { //
			 * Create a temporary file to store the video File tempFile =
			 * File.createTempFile("preview", ".mp4"); try (FileOutputStream fos = new
			 * FileOutputStream(tempFile)) { fos.write(videoContent); }
			 * 
			 * // Create media player Media media = new Media(tempFile.toURI().toString());
			 * MediaPlayer mediaPlayer = new MediaPlayer(media); MediaView mediaView = new
			 * MediaView(mediaPlayer);
			 * 
			 * // Set dimensions mediaView.setFitWidth(217); mediaView.setFitHeight(160);
			 * mediaView.setPreserveRatio(true);
			 * 
			 * // Add to container previewContainer.getChildren().add(mediaView);
			 * 
			 * // Start playing mediaPlayer.setAutoPlay(true); mediaPlayer.setMute(true);
			 * mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			 * 
			 * // Clean up temp file when done mediaPlayer.setOnEndOfMedia(() -> {
			 * tempFile.delete(); }); } else { throw new
			 * IOException("No video content available"); } } catch (Exception e) { //
			 * Fallback to icon if preview fails VBox videoPreview = new VBox(10);
			 * videoPreview.setAlignment(Pos.CENTER);
			 * 
			 * FontIcon videoIcon = new FontIcon("fas-video"); videoIcon.setIconSize(60);
			 * videoIcon.setIconColor(Color.web("#333333"));
			 * 
			 * Label videoName = new Label(fileInfo.getName());
			 * videoName.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
			 * videoName.setMaxWidth(200); videoName.setWrapText(true);
			 * 
			 * videoPreview.getChildren().addAll(videoIcon, videoName);
			 * previewContainer.getChildren().add(videoPreview); } }
			 */ else if (fileType.equals("image"))
		{
			try
			{
				byte[] imageContent = fileInfoController.getFileContent(fileInfo);
				if (imageContent != null)
				{
					Image image = new Image(new ByteArrayInputStream(imageContent));
					ImageView imageView = new ImageView(image);

					// Set max dimensions but preserve aspect ratio
					imageView.setFitWidth(217);
					imageView.setFitHeight(160);
					imageView.setPreserveRatio(true);
					imageView.setSmooth(true);

					// Center the image in the container
					StackPane.setAlignment(imageView, Pos.CENTER);

					previewContainer.getChildren().add(imageView);
				} else
				{
					throw new IOException("No image content available");
				}
			} catch (Exception e)
			{
				// If image loading fails, show placeholder
				VBox imagePreview = new VBox(10);
				imagePreview.setAlignment(Pos.CENTER);

				FontIcon imageIcon = new FontIcon("fas-image");
				imageIcon.setIconSize(60);
				imageIcon.setIconColor(Color.web("#333333"));

				Label imageName = new Label(fileInfo.getName());
				imageName.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
				imageName.setMaxWidth(200);
				imageName.setWrapText(true);

				imagePreview.getChildren().addAll(imageIcon, imageName);
				previewContainer.getChildren().add(imagePreview);
			}
		} else
		{
			// Generic file preview
			VBox genericPreview = new VBox(10);
			genericPreview.setAlignment(Pos.CENTER);

			FontIcon fileIcon = new FontIcon("fas-file");
			fileIcon.setIconSize(60);
			fileIcon.setIconColor(Color.web("#333333"));

			Label fileName = new Label(fileInfo.getName());
			fileName.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
			fileName.setMaxWidth(200);
			fileName.setWrapText(true);

			genericPreview.getChildren().addAll(fileIcon, fileName);
			previewContainer.getChildren().add(genericPreview);
		}

		// File name and actions area
		HBox fileActions = new HBox();
		fileActions.getStyleClass().add("file-actions");
		fileActions.setAlignment(Pos.CENTER_LEFT);

		Label fileName = new Label(fileInfo.getName());
		fileName.getStyleClass().add("file-name");
		fileName.setMaxWidth(140);
		fileName.setWrapText(true);
		fileName.setTextOverrun(OverrunStyle.ELLIPSIS); // Add ellipsis for long names
		HBox.setHgrow(fileName, Priority.ALWAYS);

		Button downloadBtn = new Button();
		FontIcon downloadIcon = new FontIcon("fas-download");
		downloadIcon.setIconSize(16);
		downloadIcon.setIconColor(Color.WHITE);
		downloadBtn.setGraphic(downloadIcon);
		downloadBtn.getStyleClass().add("action-button-small");
		downloadBtn.setOnAction(e -> downloadFile(fileInfo));

		Button deleteBtn = new Button();
		FontIcon deleteIcon = new FontIcon("fas-trash");
		deleteIcon.setIconSize(16);
		deleteIcon.setIconColor(Color.WHITE);
		deleteBtn.setGraphic(deleteIcon);
		deleteBtn.getStyleClass().add("action-button-small");
		deleteBtn.setOnAction(e -> deleteFile(fileInfo));

		fileActions.getChildren().addAll(fileName, downloadBtn, deleteBtn);

		fileBox.getChildren().addAll(previewContainer, fileActions);

		return fileBox;
	}

	private Stage getStage()
	{
		return (Stage) getScene().getWindow();
	}

	private void uploadFiles()
	{
		if (currentMaintenance == null)
		{
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Upload Error");
			alert.setHeaderText("Er is geen onderhoud geselecteerd");
			alert.setContentText("Er is geen onderhoud geselecteerd om bestanden aan toe te voegen.");
			alert.initOwner(getStage());
			alert.showAndWait();
			return;
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Upload Files");

		// Set supported file types
		FileChooser.ExtensionFilter allSupportedFilter = new FileChooser.ExtensionFilter("Alle ondersteunde bestanden",
				"*.pdf", "*.jpg", "*.jpeg", "*.png", "*.gif");
		FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Bestanden", "*.pdf");
		FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Foto Bestanden", "*.jpg", "*.jpeg",
				"*.png", "*.gif");
		/*
		 * FileChooser.ExtensionFilter videoFilter = new
		 * FileChooser.ExtensionFilter("Video Bestanden", "*.mp4", "*.mov", "*.avi");
		 */

		fileChooser.getExtensionFilters().addAll(allSupportedFilter, pdfFilter, imageFilter /* ,videoFilter */);
		fileChooser.setSelectedExtensionFilter(allSupportedFilter); // Set default filter

		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(getStage());

		if (selectedFiles != null && !selectedFiles.isEmpty())
		{
			// Get maintenance entity for database relationship
			Maintenance maintenance = maintenanceController.getMaintenance(currentMaintenance.id());
			if (maintenance == null)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Upload Error");
				alert.setHeaderText("Database Error");
				alert.setContentText("Could not find maintenance in database.");
				alert.initOwner(getStage());
				alert.showAndWait();
				return;
			}

			// Track failed uploads
			List<String> failedUploads = new ArrayList<>();
			List<FileInfo> successfulUploads = new ArrayList<>();

			// Try to upload each file
			for (File file : selectedFiles)
			{
				try
				{
					// Validate file type
					String fileType = getFileType(file.getName());
					if (!isValidFileType(fileType))
					{
						failedUploads.add(String.format(
								"- %s: Bestandstype niet ondersteund. Alleen PDF en afbeelding bestanden zijn toegestaan.",
								file.getName()));
						continue;
					}

					// Create FileInfo entity
					FileInfo newFile = new FileInfo(file.getName(), fileType, null, maintenance);

					// Try to save file content to database
					fileInfoController.saveFileContent(file, newFile);

					// If successful, add to successful uploads
					successfulUploads.add(newFile);

				} catch (Exception e)
				{
					String errorMessage = e.getMessage();
					if (errorMessage.contains("Packet for query is too large"))
					{
						errorMessage = "Het bestand is te groot voor de database (max 65MB)";
					}
					failedUploads.add(String.format("- %s: %s", file.getName(), errorMessage));
				}
			}

			// If we have successful uploads, create the files section and add the files
			if (!successfulUploads.isEmpty())
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

				// Add successful uploads to UI and list
				for (FileInfo file : successfulUploads)
				{
					currentFiles.add(file);
					filesContainer.getChildren().add(createFileBox(file));
				}
			}

			// Show summary of failed uploads if any
			if (!failedUploads.isEmpty())
			{
				StringBuilder errorMessage = new StringBuilder(
						"De volgende bestanden konden niet worden geüpload:\n\n");
				errorMessage.append(String.join("\n", failedUploads));
				errorMessage.append("\n\nControleer of de bestanden het juiste type zijn en niet te groot zijn.");

				// Show the alert on the JavaFX Application Thread
				Platform.runLater(() ->
				{
					try
					{
						Alert errorAlert = new Alert(Alert.AlertType.ERROR);
						errorAlert.setTitle("Upload Fout");
						errorAlert.setHeaderText("Upload mislukt");
						errorAlert.setContentText(errorMessage.toString());
						errorAlert.initOwner(getStage());
						errorAlert.showAndWait();
					} catch (Exception e)
					{
						System.err.println("Error showing alert: " + e.getMessage());
					}
				});
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
		} /*
			 * else if (lowerCaseName.matches(".*\\.(mp4|mov|avi)$")) { return "video"; }
			 */
		return "other";
	}

	private boolean isValidFileType(String fileType)
	{
		return fileType.equals("pdf") || fileType.equals("image") || fileType.equals("video");
	}

	private void downloadFile(FileInfo fileInfo)
	{
		// Choose the download location
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Selecteer download locatie");
		File selectedDirectory = directoryChooser.showDialog(getStage());

		if (selectedDirectory != null)
		{
			try
			{
				// Get file content from database
				byte[] content = fileInfoController.getFileContent(fileInfo);
				if (content == null)
				{
					Alert errorAlert = new Alert(Alert.AlertType.WARNING);
					errorAlert.setTitle("Download Error");
					errorAlert.setHeaderText("Bestand niet beschikbaar");
					errorAlert.setContentText("Het bestand kon niet worden gedownload.");
					errorAlert.initOwner(getStage());
					errorAlert.showAndWait();
					return;
				}

				// Create target file
				File targetFile = new File(selectedDirectory.getAbsolutePath() + File.separator + fileInfo.getName());

				// Write content to file
				try (FileOutputStream fos = new FileOutputStream(targetFile))
				{
					fos.write(content);
				}

				Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
				successAlert.setTitle("Download Succesvol");
				successAlert.setHeaderText("Bestand gedownload");
				successAlert.setContentText("Bestand is gedownload naar: " + targetFile.getAbsolutePath());
				successAlert.initOwner(getStage());
				successAlert.showAndWait();

			} catch (IOException e)
			{
				Alert errorAlert = new Alert(Alert.AlertType.ERROR);
				errorAlert.setTitle("Download Error");
				errorAlert.setHeaderText("Download mislukt");
				errorAlert.setContentText("Fout tijdens downloaden: " + e.getMessage());
				errorAlert.initOwner(getStage());
				errorAlert.showAndWait();
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
		confirmAlert.initOwner(getStage());

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
				Alert errorAlert = new Alert(Alert.AlertType.ERROR);
				errorAlert.setTitle("Verwijderen Fout");
				errorAlert.setHeaderText("Verwijderen mislukt");
				errorAlert.setContentText("Fout tijdens verwijderen: " + e.getMessage());
				errorAlert.initOwner(getStage());
				errorAlert.showAndWait();
			}
		}
	}
}