import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuTextField extends MenuButton {

    String currentField = "";

    private int size;

    private int carret = 0;

    public MenuTextField(int x, int y,String text, int size, String choice, MenuButton.TextType textType) {
        super(x,y,100, 100, text, textType);
        this.choice = choice;
        this.size = size;



    }



    private void enter() {
        Settings.put("name", currentField);
        Menu.switchMenu("main");
    }

    public void tick() {
        if (hideButton) {
            return;
        }

        if (Main.isKeyDown(InputAction.Default)) {
            Main.deactivateKey(InputAction.Default);



            switch (Main.lastKey) {
                case "Enter":
                    enter();

                    break;
                case "Space":
                    if (currentField.length() < Settings.MAX_NAME_LENGTH) {
                        currentField = currentField + "_";
                        carret++;
                    }
                    break;
                case "Backspace":
                    if (currentField.length() > 0) {

                        int pos = Math.min(carret, currentField.length()-1);
                        currentField = currentField.substring(0, pos) +currentField.substring(pos+1) ;
                        carret--;
                    }
                    break;
                case "Delete":
                    if (currentField.length() > 0) {

                        int pos = Math.min(carret, currentField.length()-1);
                        currentField = currentField.substring(0, pos) +currentField.substring(pos+1) ;

                    }
                    break;

                case "Left":
                    carret--;

                    break;
                case "Right":
                    carret++;

                    break;
                default:
                    if (Main.lastKey.length() ==1) {
                        if (currentField.length() < Settings.MAX_NAME_LENGTH) {


                            if (currentField.length() > 0) {
                                if (carret < currentField.length()) {
                                    currentField = currentField.substring(0, carret)+ Main.lastKey + currentField.substring(carret + 1);
                                } else {
                                    currentField = currentField+ Main.lastKey;
                                    carret = currentField.length();
                                }

                            } else {
                                currentField = currentField+ Main.lastKey;
                                carret = currentField.length();
                            }



                        }
                    }
                    break;
            }

            Main.lastKey = "";
            currentField = currentField + Main.lastKey;
        }
        carret = Math.min(currentField.length(),carret);
        carret = Math.max(0,carret);


    }



    @Override
    public void render(GraphicsContext g) {
        if (hideButton) {
            return;
        }

            g.setFill(Color.WHITE);
            g.setFont(new Font("Monospaced Regular",Main.correctUnit(50)));
            g.fillText(currentField, Main.correctUnit(x), Main.correctUnit(y));





            boolean hasCarreted = false;
            String pos = "";
            for (int i = 0; i < currentField.length(); i++) {

                if (i == carret) {
                    pos = pos + "_";
                    hasCarreted = true;
                } else {
                    pos = pos + " ";
                }
            }

            if (!hasCarreted && currentField.length() < Settings.MAX_NAME_LENGTH) {
                pos = pos + "_";
            }


            g.fillText(pos, Main.correctUnit(x), Main.correctUnit(y));



    }
}
