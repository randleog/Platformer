public enum InputAction {
    Default() {
      public String toString() {
          return "Default";
      }
    },
    Right() {
        public String toString() {
            return "Right";
        }
    },
    Left() {
        public String toString() {
            return "Left";
        }
    },
    Up() {
        public String toString() {
            return "Up";
        }
    },
    Down() {
        public String toString() {
            return "Down";
        }
    },
    Sprint() {
        public String toString() {
            return "Sprint";
        }
    };

    public static boolean isXType(InputAction action) {
        return(action == InputAction.Right || action == InputAction.Left);

    }

    public static boolean isYType(InputAction action) {
        return(action == InputAction.Down || action == InputAction.Up);

    }

    public static boolean isWallType(InputAction action) {
        return(action == InputAction.Right || action == InputAction.Left|| action == InputAction.Up || action == InputAction.Down);

    }
}
