
public class VideoDepartment extends Department {

	public static class VideoBuilder extends Department.DepartmentBuilder {

		public VideoBuilder(String name, int ID) {
			super(name, ID);
		}

		public Department build() {
			return new VideoDepartment(this);
		}
	}
	
	private VideoDepartment(VideoBuilder videoBuilder) {
		super(videoBuilder);
	}
	
	public void accept(ShoppingCart s) {
		s.visit(this);
	}
}
