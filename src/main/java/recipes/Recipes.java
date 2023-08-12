package recipes;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import recipes.entity.Recipe;
import recipes.exception.DbException;
import recipes.service.RecipeService; 

public class Recipes {
	private Scanner scanner = new Scanner(System.in);
	private RecipeService recipeService = new RecipeService();

	private List<String> operations = List.of("1) Create and populate all tables", "2) add a recipe");

	public static void main(String[] args) {
		new Recipes().displayMenu();
	}

	private void displayMenu() {
		boolean done = false;

		while (!done) {
			int operation = getOperation();

			try {
				switch (operation) {
					case -1:
						done = exitMenu();
						break;

					case 1:
						createTables();
						break;

					case 2:
						addRecipe();
						break;

					default:
						System.out.println("\n" + operation + " is not valid. Try again.");
						break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
		}
	}

	private void addRecipe() {
		String name = getStringInput("Enter the recipe name");
		String notes = getStringInput("Enter the recipe notes");
		Integer numServings = getIntInput("Enter Number of servings");
		Integer prepminutes = getIntInput("Enter prep time in minutes");
		Integer cookminutes = getIntInput("Enter cook time in minutes");

		LocalTime prepTime = minutesToLocalTime(prepminutes);
		LocalTime cookTime = minutesToLocalTime(cookminutes);

		Recipe recipe = new Recipe();
		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(numServings);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);

		Recipes dbRecipe = recipeService.addRecipe(recipe);

		System.out.println("You added this recipe:\n" + dbRecipe);
	}

	private LocalTime minutesToLocalTime(Integer numminutes) {
		int min = Objects.isNull(numminutes) ? 0 : numminutes;
		int hours = min / 60;
		int minutes = min % 60;

		return LocalTime.of(hours, minutes);
	}

	private void createTables() {
		recipeService.createAndPopulateTables();
		System.out.println("\nTables created and populated!");
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu. TTFN!");
		return true;
	}

	private int getOperation() {
		printOperations();
		Integer op = getIntInput("\nEnter an operation number (press Enter to quit)");

		return Objects.isNull(op) ? -1 : op;
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	}

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do:");
		operations.forEach(op -> System.out.println("   " + op));
	}
}
