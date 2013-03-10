package ca.uwaterloo.joos.checker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HierarchyChecker {
	public static HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();
   
	public HierarchyChecker(HierarchyBuilder hierarchyBuilder) throws Exception {
		this.hierarchyBuilder = hierarchyBuilder;
		//System.out.println("hierarchyBuilder"+hierarchyBuilder.getHierarchyMap().size());
		//System.out.println("checking @@@@");
		//checkCycle();
		checkImplements();
		checkExtends();
		
	}

	private void checkImplements() throws Exception {
		Iterator it = hierarchyBuilder.getImplementMap().entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			List<String> interfaeces = (List<String>) pairs.getValue();
			for (String interfaceName : interfaeces) {
				if (!hierarchyBuilder.getInterfaces().contains(interfaceName)) {
					throw new Exception("implement must be a interface");
				}
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	private void checkExtends() throws Exception {

		Iterator it = hierarchyBuilder.getHierarchyMap().entrySet().iterator();
		while (it.hasNext()) {
			Set<String> superClain = new HashSet<String>();
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println("adding @@@@" + (String) pairs.getKey());
			superClain.add((String) pairs.getKey());

			if (superClain.contains((String) pairs.getValue())) {
				throw new Exception("extend cycle");
			} else {
				System.out.println("adding @@@@" + (String) pairs.getValue());
				superClain.add((String) pairs.getValue());
			}
			String chain = (String) pairs.getValue();

			while (this.hierarchyBuilder.getHierarchyMap().containsKey(chain)) {
				if (superClain.contains((String) this.hierarchyBuilder.getHierarchyMap().get(chain))) {
					throw new Exception("extend cycle");
				} else {
					System.out.println("adding @@@@" + (String) this.hierarchyBuilder.getHierarchyMap().get(chain));
					superClain.add(this.hierarchyBuilder.getHierarchyMap().get(chain));
				}
				chain = this.hierarchyBuilder.getHierarchyMap().get(chain);

			}
			@SuppressWarnings("rawtypes")
		
			String className = (String) pairs.getKey();

			// @SuppressWarnings("unchecked")
			String SUPER = (String) pairs.getValue();

			if (hierarchyBuilder.getInterfaces().contains(SUPER) && (hierarchyBuilder.getClasses().contains(className))) {
				throw new Exception("a class" + className + " can not extend an interface" + SUPER);
			}
			if (hierarchyBuilder.getInterfaces().contains(SUPER) && (hierarchyBuilder.getClasses().contains(SUPER))) {
				throw new Exception("an interface" + className + " can not extend a class" + SUPER);
			}
			if (hierarchyBuilder.getFinals().contains(SUPER)) {
				throw new Exception("can not extend final" + SUPER);
			}

			it.remove(); // avoids a ConcurrentModificationException
		}
	}

}
