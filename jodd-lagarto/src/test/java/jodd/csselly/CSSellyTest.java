// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.csselly;

import jodd.csselly.selector.AttributeSelector;
import jodd.csselly.selector.PseudoClassSelector;
import jodd.csselly.selector.PseudoFunctionExpression;
import jodd.csselly.selector.PseudoFunctionSelector;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CSSellyTest {

	@Test
	public void testSingleSelectors() {
		CSSelly lexer = new CSSelly("  div  ");
		assertEquals("div", CSSelly.toString(lexer.parse()));

		lexer = new CSSelly("div#id");
		assertEquals("div#id", CSSelly.toString(lexer.parse()));

		lexer = new CSSelly("div.klas");
		assertEquals("div.klas", CSSelly.toString(lexer.parse()));

		lexer = new CSSelly("div.k1.k2");
		List<CssSelector> selectors = lexer.parse();
		assertEquals("div.k1.k2", CSSelly.toString(selectors));
		assertEquals(1, selectors.size());
		assertEquals(2, selectors.get(0).selectorsCount());

		lexer = new CSSelly("*");
		assertEquals("*", CSSelly.toString(lexer.parse()));

		lexer = new CSSelly("*.kl");
		assertEquals("*.kl", CSSelly.toString(lexer.parse()));

		lexer = new CSSelly(".kl");
		assertEquals("*.kl", CSSelly.toString(lexer.parse()));

		lexer = new CSSelly("#idi");
		selectors = lexer.parse();
		assertEquals("*#idi", CSSelly.toString(selectors));
		assertEquals(1, selectors.size());
		assertEquals(1, selectors.get(0).selectorsCount());
	}

	@Test
	public void testMultipleSelectors() {
		CSSelly lexer = new CSSelly("  div  b#xo  foo.solid #jodd * #bib.box.red  ");
		List<CssSelector> selectors = lexer.parse();

		assertEquals("div b#xo foo.solid *#jodd * *#bib.box.red", CSSelly.toString(selectors));
		assertEquals(6, selectors.size());
		assertEquals(0, selectors.get(4).selectorsCount());
		assertEquals(3, selectors.get(5).selectorsCount());

		CssSelector first = selectors.get(0);
		CssSelector second = selectors.get(1);
		CssSelector third = selectors.get(2);
		CssSelector fourth = selectors.get(3);
		CssSelector fifth = selectors.get(4);
		CssSelector sixt = selectors.get(5);

		assertNull(first.getPrevCssSelector());
		assertEquals(second, first.getNextCssSelector());

		assertEquals(first, second.getPrevCssSelector());
		assertEquals(third, second.getNextCssSelector());

		assertEquals(second, third.getPrevCssSelector());
		assertEquals(fourth, third.getNextCssSelector());

		assertEquals(third, fourth.getPrevCssSelector());
		assertEquals(fifth, fourth.getNextCssSelector());

		assertEquals(fourth, fifth.getPrevCssSelector());
		assertEquals(sixt, fifth.getNextCssSelector());

		assertEquals(fifth, sixt.getPrevCssSelector());
		assertNull(sixt.getNextCssSelector());
	}

	@Test
	public void testAttributes() {
		CSSelly lexer = new CSSelly("div[a1='123']");
		List<CssSelector> selectors = lexer.parse();
		assertEquals(1, selectors.size());
		assertEquals("div[a1='123']", CSSelly.toString(selectors));

		CssSelector cssSelector = selectors.get(0);
		assertEquals("div", cssSelector.getElement());
		assertEquals(1, cssSelector.selectorsCount());

		Selector selector = cssSelector.getSelector(0);
		assertEquals(Selector.Type.ATTRIBUTE, selector.getType());
		AttributeSelector attributeSelector = (AttributeSelector) selector;
		assertEquals("a1", attributeSelector.getName());
		assertEquals("=", attributeSelector.getMatch().getSign());
		assertEquals("123", attributeSelector.getValue());

		lexer = new CSSelly("div[ a1 = \"123\" ]");
		selectors = lexer.parse();
		assertEquals("div[a1=\"123\"]", CSSelly.toString(selectors));
	}

	@Test
	public void testCombinators() {
		CSSelly lexer = new CSSelly("div b");
		List<CssSelector> selectors = lexer.parse();
		assertEquals(2, selectors.size());

		CssSelector cssSelector = selectors.get(0);
		assertEquals(Combinator.DESCENDANT, cssSelector.getCombinator());
		cssSelector = selectors.get(1);
		assertNull(cssSelector.getCombinator());


		lexer = new CSSelly("div > b");
		selectors = lexer.parse();
		assertEquals(2, selectors.size());

		cssSelector = selectors.get(0);
		assertEquals(Combinator.CHILD, cssSelector.getCombinator());
		cssSelector = selectors.get(1);
		assertNull(cssSelector.getCombinator());


		lexer = new CSSelly("div>b");
		selectors = lexer.parse();
		assertEquals(2, selectors.size());

		cssSelector = selectors.get(0);
		assertEquals(Combinator.CHILD, cssSelector.getCombinator());
		cssSelector = selectors.get(1);
		assertNull(cssSelector.getCombinator());


		lexer = new CSSelly("div>b + span ~ i");
		selectors = lexer.parse();
		assertEquals(4, selectors.size());

		cssSelector = selectors.get(0);
		assertEquals(Combinator.CHILD, cssSelector.getCombinator());
		cssSelector = selectors.get(1);
		assertEquals(Combinator.ADJACENT_SIBLING, cssSelector.getCombinator());
		cssSelector = selectors.get(2);
		assertEquals(Combinator.GENERAL_SIBLING, cssSelector.getCombinator());
		cssSelector = selectors.get(3);
		assertNull(cssSelector.getCombinator());
	}

	@Test
	public void testPseudoClasses() {
		CSSelly lexer = new CSSelly("div:first-child");
		List<CssSelector> selectors = lexer.parse();
		assertEquals(1, selectors.size());
		assertEquals("div:first-child", CSSelly.toString(selectors));

		CssSelector cssSelector = selectors.get(0);
		assertEquals(1, cssSelector.selectorsCount());
		PseudoClassSelector psc = (PseudoClassSelector) cssSelector.getSelector(0);
		assertEquals("first-child", psc.getPseudoClass().getPseudoClassName());
	}

	@Test
	public void testPseudoFunctions() {
		CSSelly lexer = new CSSelly("div:nth-child(2n+1)");
		List<CssSelector> selectors = lexer.parse();
		assertEquals(1, selectors.size());
		assertEquals("div:nth-child(2n+1)", CSSelly.toString(selectors));

		CssSelector cssSelector = selectors.get(0);
		assertEquals(1, cssSelector.selectorsCount());
		PseudoFunctionSelector pfns = (PseudoFunctionSelector) cssSelector.getSelector(0);
		assertEquals("nth-child", pfns.getPseudoFunction().getPseudoFunctionName());
		PseudoFunctionExpression pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(2, pfe.getValueA());
		assertEquals(1, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(odd)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		assertEquals("nth-child", pfns.getPseudoFunction().getPseudoFunctionName());
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(2, pfe.getValueA());
		assertEquals(1, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(even)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		assertEquals("nth-child", pfns.getPseudoFunction().getPseudoFunctionName());
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(2, pfe.getValueA());
		assertEquals(0, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(10n-1)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(10, pfe.getValueA());
		assertEquals(-1, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(10n+9)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(10, pfe.getValueA());
		assertEquals(9, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(0n+5)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(0, pfe.getValueA());
		assertEquals(5, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(5)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(0, pfe.getValueA());
		assertEquals(5, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(1n + 0)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(1, pfe.getValueA());
		assertEquals(0, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(n + 0)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(1, pfe.getValueA());
		assertEquals(0, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(n)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(1, pfe.getValueA());
		assertEquals(0, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(2n+0)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(2, pfe.getValueA());
		assertEquals(0, pfe.getValueB());

		lexer = new CSSelly("div:nth-child(2n)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(2, pfe.getValueA());
		assertEquals(0, pfe.getValueB());

		lexer = new CSSelly("div:nth-child( 3n + 1 )");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(3, pfe.getValueA());
		assertEquals(1, pfe.getValueB());

		lexer = new CSSelly("div:nth-child( +3n - 2 )");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(3, pfe.getValueA());
		assertEquals(-2, pfe.getValueB());

		lexer = new CSSelly("div:nth-child( -n+ 6)");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(-1, pfe.getValueA());
		assertEquals(6, pfe.getValueB());

		lexer = new CSSelly("div:nth-child( +6 )");
		pfns = (PseudoFunctionSelector) lexer.parse().get(0).getSelector(0);
		pfe = (PseudoFunctionExpression) pfns.getParsedExpression();
		assertEquals(0, pfe.getValueA());
		assertEquals(6, pfe.getValueB());
	}

	@Test
	public void testErrors() {
		try {
			CSSelly lexer = new CSSelly("div ^ b");
			lexer.parse();
			fail();
		} catch (CSSellyException ex) {
		}

		try {
			CSSelly lexer = new CSSelly("div:wrong-pseudo-class-name");
			lexer.parse();
			fail();
		} catch (CSSellyException ex) {
		}

		try {
			CSSelly lexer = new CSSelly("div:nth-child(xxx)");
			lexer.parse();
		} catch (CSSellyException ex) {
		}

	}

	@Test
	public void testUppercaseClassNames() {
		CSSelly lexer = new CSSelly("div.fooBar");
		List<CssSelector> selectorList = lexer.parse();
		assertEquals(1, selectorList.size());
	}

	@Test
	public void testEscape() {

		// element
		CSSelly lexer = new CSSelly("itunes\\:image");
		List<CssSelector> selectors = lexer.parse();

		assertEquals(1, selectors.size());
		CssSelector cssSelector = selectors.get(0);

		assertEquals("itunes:image", cssSelector.getElement());


		// attribute

		lexer = new CSSelly("itunes\\:image#foo\\:bar");
		selectors = lexer.parse();

		assertEquals(1, selectors.size());
		cssSelector = selectors.get(0);

		assertEquals("itunes:image", cssSelector.getElement());
		Selector selector = cssSelector.getSelector(0);

		AttributeSelector attributeSelector = (AttributeSelector)selector;

		assertEquals("foo:bar", attributeSelector.getValue());

	}
}
