
public class MusicDepartment extends Department {
	
	public static class MusicBuilder extends Department.DepartmentBuilder {

		public MusicBuilder(String name, int ID) {
			super(name, ID);
		}

		public Department build() {
			return new MusicDepartment(this);
		}
	}
	
	private MusicDepartment(MusicBuilder musicBuilder) {
		super(musicBuilder);
	}
	
	public void accept(ShoppingCart s) {
		s.visit(this);
	}
}
