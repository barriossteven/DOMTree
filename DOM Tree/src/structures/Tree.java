package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		if (sc == null){
			return;
		}
		
		Stack<TagNode> tags = new Stack<TagNode>();
		//System.out.println(getHTML);
		String ptrLine = sc.nextLine();
		root = new TagNode("html", null, null);
		tags.push(root);
		//html first item in stack

		while (sc.hasNextLine()) {
			ptrLine = sc.nextLine();

			if (ptrLine.contains("<") && ptrLine.contains(">") && ptrLine.contains("/")) {
				tags.pop();
			} else if (ptrLine.contains("<") && ptrLine.contains(">") && !ptrLine.contains("/")) {
				if (tags.peek().firstChild == null) {
					TagNode tmp = new TagNode(ptrLine.replace("<", "").replace(">", ""), null, null);
					tags.peek().firstChild = tmp;
					tags.push(tmp);
				} else { // must traverse to "right-most" sibling
					TagNode ptrTag = tags.peek().firstChild;

					while (ptrTag.sibling != null){ // get rightmost sibling
						ptrTag = ptrTag.sibling;
					}

					TagNode tmp = new TagNode(ptrLine.replace("<", "").replace(">", ""), null, null);
					ptrTag.sibling = tmp;
					tags.push(tmp);
					//System.out.println(ptrLine);
				}
			} else {
				if (tags.peek().firstChild == null) {
					tags.peek().firstChild = new TagNode(ptrLine, null, null);
				} else {
					TagNode ptrTag = tags.peek().firstChild;

					while (ptrTag.sibling != null){
						ptrTag = ptrTag.sibling;
					}

					ptrTag.sibling = new TagNode(ptrLine, null, null);
				}
			}
		}
		
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if (root == null || oldTag == null || newTag == null){
			return;
		}
		
		replaceTag(oldTag, newTag, root);
		
		/*
		 * if (root == null || oldTag == null|| newTag == null){
			return;
		}
		while (
		
		*/
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		if(row < 1){
			return;
		}
		
		boldRow(row,0,root,root.firstChild);
		
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */

	public void removeTag(String tag) {
		if (root == null)
			return;
		else 
			while (containsTag(tag, root))
				removeTag(tag, root, root.firstChild);
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */

	public void addTag(String word, String tag) {
		if(root == null || word == null || tag == null){
			return;
		}else{
			 addTag(word, tag, root.firstChild);
		}
	}
		
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	private void replaceTag(String oldTag, String newTag, TagNode ptr) {
		if (ptr == null){
			return;
		}else if (ptr.tag.compareTo(oldTag) == 0){
			ptr.tag = newTag;
		}

		replaceTag(oldTag, newTag, ptr.firstChild);
		replaceTag(oldTag, newTag, ptr.sibling);
	}
	
	private void boldRow(int row, int count,TagNode prev, TagNode ptr) {
		if(ptr == null) {
			return;
		} 
		if(ptr.tag.equals("tr")) {
			count++;
		} 
		
		if(count == row && ptr.firstChild == null) {
			//insert tag after prev before ptr
			prev.firstChild = new TagNode("b", ptr, null);
		} 

		boldRow(row, count, ptr, ptr.firstChild); 
		boldRow(row, count, ptr, ptr.sibling);
	}
	
	private void removeTag(String tag, TagNode prev, TagNode ptr) {
		if (ptr == null || prev == null){
			return;
		} else if (ptr.tag.equals(tag)){

			if (tag.equals("ul") || tag.equals("ol")){
				removeTagList(ptr.firstChild); 
			}
			if (prev.firstChild == ptr) {
				prev.firstChild = ptr.firstChild;
				//System.out.println(prev.firstchild.tag);
				addSib(ptr.firstChild, ptr.sibling);
			} else if (prev.sibling == ptr) {
				addSib(ptr.firstChild, ptr.sibling);
				prev.sibling = ptr.firstChild;
				//System.out.println(prev.sibling.tag);
				
				/*if (prev.firstChild == ptr) {
					prev.firstChild = ptr.firstChild;
					addSib(ptr.firstChild, ptr.sibling);
				} else if (prev.sibling == ptr) {
					addSib(ptr.firstChild, ptr.sibling);
					prev.sibling = ptr.firstChild;
					*/
			}

			return;
		}

		prev = ptr;
		
		removeTag(tag, prev, ptr.firstChild);
		removeTag(tag, prev, ptr.sibling);
	}

	
	private boolean containsTag(String tag, TagNode ptr) {
		if (ptr == null){
			return false;
		}else if (ptr.tag.compareTo(tag) == 0){
			return true;
		}
		return containsTag(tag, ptr.firstChild) || containsTag(tag, ptr.sibling);
	}

	private TagNode getSib (TagNode ptr) {
		while (ptr.sibling != null){
			ptr = ptr.sibling;
		}
		return ptr;
	}

	private void addSib (TagNode ptr, TagNode newSibling) {
		ptr = getSib(ptr);
		ptr.sibling = newSibling;
	}
	
	private void removeTagList(TagNode ptr) {
		if (ptr == null){
			return;
		}else if (ptr.tag.compareTo("li") == 0){
			ptr.tag = "p";
		}
		removeTagList(ptr.sibling);
	}
	
	
	
	private void addTag(String word, String tag, TagNode ptr){

		if(ptr == null){
			return;
		}else if(ptr.tag.toLowerCase().contains(word.toLowerCase())){			
			if(ptr.tag.equalsIgnoreCase(word)){
				//tag is only word
				String tmp = ptr.tag;
				ptr.tag = tag;
				ptr.firstChild = new TagNode (tmp, ptr.firstChild, null);
			}else if(ptr.tag.toLowerCase().contains(word.toLowerCase())){

				TagNode sib = ptr.sibling;

				String before = ptr.tag.substring(0, ptr.tag.toLowerCase().indexOf(word.toLowerCase()));
				String after = ptr.tag.substring(ptr.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
				String punctuation = "";
				String text = ptr.tag.substring(ptr.tag.toLowerCase().indexOf(word.toLowerCase()), ptr.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
				boolean onePunc = false;
				if(after.charAt(0) == '!' || after.charAt(0) == '?' || after.charAt(0) == '.' || after.charAt(0) == ','){
					onePunc = true;
				}

				boolean twoPunc = false;
				if(after.length() >1){
					if(onePunc && (after.charAt(1) == '!' || after.charAt(1) == '?' || after.charAt(1) == '.' || after.charAt(1) == ',')){
						twoPunc = true;
					}
				}

				if(after.length() > 0){
					if(after.length() > 0 && onePunc && !twoPunc){
						punctuation = "" + after.charAt(0);
						after = after.substring(1);
					}
				}

				if(after.length() == 0 || (after.length() >= 1 && (after.charAt(0) == ' ')) || (after.length() > 1 && onePunc && !twoPunc) ){

					if(punctuation.equals("!") || punctuation.equals(",") || punctuation.equals(".") || punctuation.equals("?")){
						text = text + punctuation;
						punctuation = "";
					}

					if(before.equals("") || before.equals(" ")){

						ptr.tag = tag;
						ptr.firstChild = new TagNode(text + punctuation, null, null);

						if(after.length() > 0){
							ptr.sibling = new TagNode(after, null, null);
						}
					}else{

						ptr.tag = before;
						ptr.sibling = new TagNode(tag, new TagNode(text + punctuation, null, null), null);

						if(after.length() > 0){
							if(sib != null){
								ptr.sibling.sibling = new TagNode(after, null, sib);
							}else{
								ptr.sibling.sibling = new TagNode(after, null, null);
							}
						}else if (sib != null){
							ptr.sibling.sibling = sib;
						}
					}
				}
			}

		}else{
			addTag(word, tag, ptr.firstChild);
			addTag(word, tag, ptr.sibling);
		}
	}
}
