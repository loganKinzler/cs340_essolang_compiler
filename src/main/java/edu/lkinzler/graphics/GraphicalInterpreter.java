package edu.lkinzler.graphics;

public class GraphicalInterpreter {
    private Integer id = 0;

    public GraphicalInterpreter() {}

    public boolean isShape(String commandParams) {
        return commandParams.matches("[^>]]");
    }

    public boolean isAnimation(String commandParams) {
        return commandParams.matches("[>]]");
    }

    public String interpretShape(String shapeParams) {
        return interpretShape(shapeParams.split(" "));
    }

    public String interpretShape(String[] shapeParams) {
        id++;

        String shape = shapeParams[0];
        String color = shapeParams[1];

        Integer x = 0, y = 0, width = 0, height = 0;
        try {
            x = Integer.parseInt(shapeParams[2]);
            y = Integer.parseInt(shapeParams[3]);
            width = Integer.parseInt(shapeParams[4]);
            height = Integer.parseInt(shapeParams[5]);
        } catch (Exception e) {
            // run a thing to fail properly when not given a proper integer
        }

        return String.format(
                "<div " +
                        "class=\\\"graphics-shape graphics-%s graphics-id-%d\\\" " +
                        "style=" +
                            "\\\"background-color: %s; " +
                            "left: %d%%; " +
                            "top: %d%%; " +
                            "width: %d%%; " +
                            "height: %d%%; " +
                        "\\\"></div>",
                shape, id, color, x, y, width, height);
    }


    public String interpretAnimation(String animationParams) {
        return interpretAnimation(animationParams.split(" "));
    }

    public String interpretAnimation(String[] animationParams) {
        String newColor = animationParams[7];

        Integer newX = 0, newY = 0, newWidth = 0, newHeight = 0;

        try {
            newX = Integer.parseInt(animationParams[8]);
            newY = Integer.parseInt(animationParams[9]);
            newWidth = Integer.parseInt(animationParams[10]);
            newHeight = Integer.parseInt(animationParams[11]);

        } catch (Exception e) {
            // run a thing to fail properly when not given a proper integer
        }


        return String.format(
                ""
                );
    }
}
