package ca.uwaterloo.joos.checker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HierarchyChecker {
	public HierarchyBuilder hierarchyBuilder;

	public HierarchyChecker(HierarchyBuilder hierarchyBuilder) throws Exception {
		this.hierarchyBuilder = hierarchyBuilder;

	}

	public void CheckHierarchy() throws Exception {
		checkImplements();
		checkExtends();
	}

	private void checkImplements() throws Exception {
		Iterator<Entry<String, List<String>>> it = hierarchyBuilder.getImplementMap().entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) it.next();
			@SuppressWarnings("unchecked")
			List<String> interfaeces = (List<String>) pairs.getValue();
			for (String interfaceName : interfaeces) {
				if (!hierarchyBuilder.getInterfaces().contains(interfaceName)) {
					throw new Exception(pairs.getKey() + "implement" + pairs.getValue() + " must be a interface");
				}
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	private void checkExtends() throws Exception {

		Iterator<Entry<String, String>> it = hierarchyBuilder.getHierarchyMap().entrySet().iterator();
		while (it.hasNext()) {
			Set<String> superClain = new HashSet<String>();
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) it.next();
			superClain.add((String) pairs.getKey());

			if (superClain.contains((String) pairs.getValue())) {
				throw new Exception("extend cycle");
			} else {
				superClain.add((String) pairs.getValue());
			}
			String chain = (String) pairs.getValue();

			while (this.hierarchyBuilder.getHierarchyMap().containsKey(chain)) {
				if (superClain.contains((String) this.hierarchyBuilder.getHierarchyMap().get(chain))) {
					throw new Exception("extend cycle");
				} else {
					superClain.add(this.hierarchyBuilder.getHierarchyMap().get(chain));
				}
				chain = this.hierarchyBuilder.getHierarchyMap().get(chain);

			}
			String className = (String) pairs.getKey();
			String SUPER = (String) pairs.getValue();

			if (hierarchyBuilder.getInterfaces().contains(SUPER) && (!hierarchyBuilder.getInterfaces().contains(className))) {
				System.out.println(hierarchyBuilder.getInterfaces());
				throw new Exception("a class" + className + " can not extend an interface" + SUPER);
			}
			if (hierarchyBuilder.getInterfaces().contains(SUPER) && (hierarchyBuilder.getClasses().contains(SUPER))) {
				throw new Exception("an interface" + className + " can not extend a class" + SUPER);
			}
			if (hierarchyBuilder.getFinals().contains(SUPER)) {
				throw new Exception("can not extend final" + SUPER);
			}

			it.remove(); 
		}
	}

}
