package fon.tags.graph;

public class MyLink {
	String edgeName;
	int weight; // should be private for good practice
	//int id;

	public MyLink(int weight, String edgeName) {
		//this.id = edgeCount++; // This is defined in the outer class.
		this.weight = weight;
		this.edgeName = edgeName;
	}

	public String toString() { // Always good for debugging
		return edgeName;
	}
	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((edgeName == null) ? 0 : edgeName.hashCode());
		result = prime * result + weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyLink other = (MyLink) obj;
		if (edgeName == null) {
			if (other.edgeName != null)
				return false;
		} else if (!edgeName.equals(other.edgeName))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}
}
