package ca.uwaterloo.joos.checker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HierarchyChecker {
	public HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();
   
	public HierarchyChecker(HierarchyBuilder hierarchyBuilder) throws Exception {
		this.hierarchyBuilder = hierarchyBuilder;
		System.out.println("checking @@@@");
		hierarchyBuilder = new HierarchyBuilder();
		checkImplements();
		checkExtends();
		checkCycle();
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

			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) it.next();
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

	private void checkCycle() throws Exception {
		Map<String, String> hierMap = hierarchyBuilder.hierarchyMap;
		System.out.println("hierMap"+hierMap.size());
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

			while (hierMap.containsKey(chain)) {
				if (superClain.contains((String) hierMap.get(chain))) {
					throw new Exception("extend cycle");
				} else {
					System.out.println("adding @@@@" + (String) hierMap.get(chain));
					superClain.add(hierMap.get(chain));
				}
				chain = hierMap.get(chain);

			}
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			it.remove(); // avoids a ConcurrentModificationException
		}
	}
}
