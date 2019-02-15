
public class SoftwareDepartment extends Department {

	public static class SoftwareBuilder extends Department.DepartmentBuilder {

		public SoftwareBuilder(String name, int ID) {
			super(name, ID);
		}

		public Department build() {
			return new SoftwareDepartment(this);
		}
	}
	
	private SoftwareDepartment(SoftwareBuilder softwareBuilder) {
		super(softwareBuilder);
	}
	
	public void accept(ShoppingCart s) {
		s.visit(this);
	}
}
