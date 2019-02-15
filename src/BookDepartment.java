
public class BookDepartment extends Department {

	public static class BookBuilder extends Department.DepartmentBuilder {

		public BookBuilder(String name, int ID) {
			super(name, ID);
		}

		public Department build() {
			return new BookDepartment(this);
		}
	}
	
	private BookDepartment(BookBuilder bookBuilder) {
		super(bookBuilder);
	}
	
	public void accept(ShoppingCart s) {
		s.visit(this);
	}
}
