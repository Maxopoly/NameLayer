package vg.civcraft.mc.namelayer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GroupRank {

	private String name;
	private int id;
	private List<GroupRank> children;
	private GroupRank parent;
	private Set<Integer> perms;

	/**
	 * For creating completly new ranks
	 */
	public GroupRank(String name, int id, GroupRank parent) {
		this.name = name;
		this.parent = parent;
		this.id = id;
		this.children = new LinkedList<>();
		this.perms = new HashSet<>();
	}

	/**
	 * This gets the parent node of this instance. This must always be not null,
	 * except when this instance is an owner and the root node of the graph tree
	 * 
	 * @return Parent node or null if no parent exists
	 */
	public GroupRank getParent() {
		return parent;
	}
	
	/**
	 * Updates the parent rank of this one. Does not add this rank as a child to the parent
	 * @param parent New parent rank
	 */
	public void setParent(GroupRank parent) {
		this.parent = parent;
	}

	public List<GroupRank> getAllParents() {
		List<GroupRank> types = new ArrayList<>();
		if (parent != null) {
			types.add(parent);
			types.addAll(parent.getAllParents());
		}
		return types;
	}

	/**
	 * Each instance has a name, which can be modified dynamically and should only
	 * be used to present a player type to a player. For internal identifying, use
	 * ids
	 * 
	 * @return Name of this instance
	 */
	public String getName() {
		return name;
	}

	/**
	 * Updates the name of this instance
	 * 
	 * @param name New name
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets all children nodes of this instance based on the graph modelling the
	 * relation ship between player types for this instances group. The list
	 * returned will always be a copy of the one used internally
	 * 
	 * @param recursive Whether children should be retrieved recursively (deep)
	 * @return All children
	 */
	public List<GroupRank> getChildren(boolean recursive) {
		if (recursive) {
			return getRecursiveChildren();
		}
		// dont let them change the list itself
		return new ArrayList<>(children);
	}

	/**
	 * Utility method to recursively collect all children of a player type
	 */
	private List<GroupRank> getRecursiveChildren() {
		// deep search
		List<GroupRank> types = new LinkedList<>();
		for (GroupRank child : children) {
			types.add(child);
			types.addAll(child.getRecursiveChildren());
		}
		return types;
	}

	/**
	 * Checks whether the given type is a direct child of this instance
	 * 
	 * @param type Possible child
	 * @return True if the given player type is a direct child, false if not
	 */
	public boolean isChildren(GroupRank type) {
		return children.contains(type);
	}

	/**
	 * Adds the given PlayerType as child to this instance
	 * 
	 * @param child PlayerType to add as child
	 * @return True if it was added successfully, false if not
	 */
	public boolean addChild(GroupRank child) {
		if (isChildren(child)) {
			return false;
		}
		children.add(child);
		return true;
	}

	/**
	 * Removes the given PlayerType as child from this instance
	 * 
	 * @param child PlayerType to remove as child
	 * @return True if it was removed successfully, false if not
	 */
	public boolean removeChild(GroupRank child) {
		if (isChildren(child)) {
			children.remove(child);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds the given permission to this instance
	 * 
	 * @param perm     Permission to add
	 */
	public void addPermission(PermissionType perm) {
		if (hasPermission(perm)) {
			// already exists
			throw new IllegalGroupStateException();
		}
		perms.add(perm.getId());
	}

	/**
	 * Removes the given permission from this instance
	 * 
	 * @param perm     Permission to remove
	 */
	public void removePermission(PermissionType perm) {
		if (parent == null) {
			// is root and shouldnt be modified
			throw new IllegalGroupStateException();
		}
		if (!perms.contains(perm.getId())) {
			// doesn't exist
			throw new IllegalGroupStateException();
		}
		perms.remove(perm.getId());
	}

	/**
	 * Checks whether this player type has the given permission
	 * 
	 * @param perm Permission to check for
	 * @return True if this player type has the given permission, false if not
	 */
	public boolean hasPermission(PermissionType perm) {
		return perms.contains(perm.getId());
	}

	/**
	 * @return Copy of the list containing all permissions this instance has
	 */
	public Set<Integer> getAllPermissions() {
		return Collections.unmodifiableSet(perms);
	}

	/**
	 * Each PlayerType has an id, which is unique for the group it is assigned to,
	 * but not unique for all groups, for example all owner root player types will
	 * always have id 0
	 * 
	 * @return Id of this instance
	 */
	public int getId() {
		return id;
	}

	/**
	 * Checks if the given player type is equal to this node or a direct/transitive
	 * parent
	 * 
	 * @param type Type to check relation true
	 * @return True if the given type is above this one in the hierarchy of the
	 *         group, false if not
	 */
	public boolean isEqualOrAbove(GroupRank type) {
		if (type == this) {
			return true;
		}
		return parent != null && parent.isEqualOrAbove(type);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GroupRank)) {
			return false;
		}
		GroupRank comp = (GroupRank) o;
		return comp.getId() == this.getId() && comp.getName().equals(this.getName());
	}

}
