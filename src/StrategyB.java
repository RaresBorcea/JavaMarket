
public class StrategyB implements Strategy {
	//comparators were used so that the needed element
	//is always on the first position
	public Item execute(WishList wishList) {
		return (Item)wishList.remove(0);
	}
}
