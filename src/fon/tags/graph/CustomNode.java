package fon.tags.graph;

public class CustomNode {
	String word; // good coding practice would have this as private

	public CustomNode(String word) {
		this.word = word;
	}

	public String toString() { // Always a good idea for debuging
		return word; // JUNG2 makes good use of these.
	}
	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		CustomNode other = (CustomNode) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
}
