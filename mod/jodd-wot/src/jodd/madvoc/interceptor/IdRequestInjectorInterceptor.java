// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.injector.ScopeData;
import jodd.madvoc.meta.In;
import jodd.madvoc.ScopeType;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.component.ScopeDataManager;

/**
 * Injects only ID request attributes and parameters that ends with '.id'.
 */
public class IdRequestInjectorInterceptor extends ActionInterceptor {

	protected static final String ATTR_NAME_ID_SUFFIX = ".id";

	@In(scope = ScopeType.CONTEXT)
	protected ScopeDataManager scopeDataManager;

	protected RequestScopeInjector requestInjector;

	@Override
	public void init() {
		requestInjector = new RequestScopeInjector(scopeDataManager) {
			@Override
			protected String getMatchedPropertyName(ScopeData.In in, String attrName) {
				if (attrName.endsWith(ATTR_NAME_ID_SUFFIX) == false) {
					return null;
				}
				return super.getMatchedPropertyName(in, attrName);
			}
		};
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		requestInjector.inject(actionRequest.getAction(), actionRequest.getHttpServletRequest());
		return actionRequest.invoke();
	}



}