public class Pet{
public static void main(String args[]){
   public  Drawable {
        void paint();
    }
    
    public class Pet {
        public void run() {
            System.out.println("Pet calling run()");
        }
    }
    
    public class Gerbil extends Pet implements Drawable {
        public void paint() {
            System.out.println("Gerbil calling paint()");
        }
    }
    
    public class Polymorphism {
        public static void main(String[] args) {
            Drawable drawable = new Gerbil();
            Pet pet = new Pet();
            Gerbil gerbil = new Gerbil();
            Drawable gerbilDrawable = gerbil;
            Pet gerbilPet = gerbil;
            Pet drawablePet = (Pet)drawable;
            
        }
    }
    }
    