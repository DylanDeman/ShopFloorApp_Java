package gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import util.AuthenticationUtil;
import util.Role;

public class ChoicePane extends GridPane
{

	private final MainLayout mainLayout;

	public ChoicePane(MainLayout mainLayout)
	{
		this.mainLayout = mainLayout;
		setupLayout();
	}

	private void setupLayout()
	{

		Text title = new Text("Kies je paneel");
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		Button maintenanceListButton = createButton("Lijst onderhouden");
		maintenanceListButton.setOnAction(e -> mainLayout.showMaintenanceList());

		Button sitesButton = createButton("Overzicht sites");
		sitesButton.setOnAction(e -> mainLayout.showSitesList());

		Button machinesButton = createButton("Machine overzicht");
		machinesButton.setOnAction(e -> mainLayout.showMachineScreen());

		this.add(sitesButton, 2, 1);
		this.add(machinesButton, 3, 1);

		if (AuthenticationUtil.hasRole(Role.ADMIN))
		{
			Button userManagementButton = createButton("Overzicht gebruikers");
			userManagementButton.setOnAction(e -> mainLayout.showUserManagementScreen());
			this.add(userManagementButton, 0, 1);
		}

		this.add(title, 0, 0, 2, 1);
		this.add(maintenanceListButton, 1, 1);

		for (javafx.scene.Node node : this.getChildren())
		{
			if (node instanceof Button)
			{
				GridPane.setMargin(node, new Insets(10));
			}
		}
	}

	private Button createButton(String text)
	{
		Button button = new Button(text);
		button.setPrefSize(200, 200);
		button.setStyle("-fx-font-size: 16px; " + "-fx-font-weight: bold; " + "-fx-background-color: #d9d9d9; "
				+ "-fx-text-fill: black; " + "-fx-background-radius: 10; " + "-fx-border-radius: 10; ");
		return button;
	}

}