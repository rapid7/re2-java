package com.logentries.re2_test;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Random;

public class GenRegExpr {
    public abstract class Operator {
        public abstract int getArity();
        public abstract String call(String ... args);
    }

    public abstract class NullaryOperator extends Operator {
        public final int getArity() {
            return 0;
        }
        public String call(String ... args) {
            assert args.length == 0;
            return callNullary();
        }
        protected abstract String callNullary();
    }

    public abstract class UnaryOperator extends Operator {
        public final int getArity() {
            return 1;
        }
        public String call(String ... args) {
            assert args.length == 1;
            return callUnary(args[0]);
        }
        protected abstract String callUnary(final String arg);
    }

    public abstract class BinaryOperator extends Operator {
        public final int getArity() {
            return 2;
        }
        public String call(String ... args) {
            assert args.length == 2;
            return callBinary(args[0], args[1]);
        }
        protected abstract String callBinary(final String leftArg, final String rightArg);
    }

    public final class ConstOperator extends NullaryOperator {
        private final String m_val;

        public ConstOperator(final String val) {
            m_val = val;
        }

        protected String callNullary() {
            return m_val;
        }

        public String toString() {
            return "" + '"' + m_val + '"';
        }
    }

    public final class OperatorUnaryStar extends UnaryOperator {
        protected String callUnary(final String arg) {
            return arg + '*';
        }

        public String toString() {
            return "*";
        }
    }

    public final class OperatorUnaryPlus extends UnaryOperator {
        protected String callUnary(final String arg) {
            return arg + '+';
        }

        public String toString() {
            return "+";
        }
    }

    public final class OperatorUnaryQM extends UnaryOperator {
        protected String callUnary(final String arg) {
            return arg + '?';
        }

        public String toString() {
            return "?";
        }
    }

    public final class OperatorBinaryConcat extends BinaryOperator {
        protected String callBinary(final String leftArg, final String rightArg) {
            return leftArg + rightArg;
        }

        public String toString() {
            return "<>";
        }
    }

    public final class OperatorBinaryPipe extends BinaryOperator {
        protected String callBinary(final String leftArg, final String rightArg) {
            return leftArg + '|' + rightArg;
        }

        public String toString() {
            return "|";
        }
    }

    /* Member Variables  */

    Random mRand = new Random();

    // Operators that are not nullary
    private List<Operator> mOps = Arrays.asList(new OperatorUnaryStar(),
                                                new OperatorUnaryPlus(),
                                                new OperatorUnaryQM(),
                                                new OperatorBinaryConcat(),
                                                new OperatorBinaryPipe()
                                               );

    // Nullary operators
    private List<Operator> mNullary;  // Generated from input

    private int mMaxAtoms;
    private int mMaxOps;

    /* Member Functions */

    public GenRegExpr(final Collection<String> consts, final int maxAtoms, final int maxOps) {
        mMaxAtoms = maxAtoms;
        mMaxOps = maxOps;
        mNullary = new ArrayList<Operator>(consts.size());
        for (String s: consts) {
            mNullary.add(new ConstOperator(s));
        }
    }

    protected String group(final String s) {
        return "(?:" + s + ")";
    }

    protected String runPostfix(Stack<Operator> opsStack) {
/*
        String ret = "Stack";
        for (Operator item: stack) {
            ret += " " + item.toString();
        }
        return ret;
*/
        Stack<String> valsStack = new Stack<String>();
        for (Operator item: opsStack) {
            switch (item.getArity()) {
            case 0:
                valsStack.push(item.call());
                break;
            case 1:
                final String arg = valsStack.pop();
                valsStack.push( group(item.call(arg)) );
                break;
            case 2:
                final String rightArg = valsStack.pop();
                final String leftArg = valsStack.pop();
                valsStack.push( group(item.call(leftArg, rightArg)) );
                break;
            default:
                assert false;
                break;
            }
        }
        assert valsStack.size() == 1;
        return valsStack.pop();
    }

    protected String genPostfix(Stack<Operator> stack, final int nstk, final int ops, final int atoms) {
        for (;;) {
            if (nstk + ops >= mMaxOps) {
                return null;
            }

            if (nstk == 1 && mRand.nextInt(2) == 0) {
                return runPostfix(stack);
            }

            if (atoms < mMaxAtoms && mRand.nextInt(2) == 0) {
                stack.push( mNullary.get(mRand.nextInt(mNullary.size())) );
                final String ret = genPostfix(stack, nstk + 1, ops, atoms + 1);
                stack.pop();
                if (ret != null) {
                    return ret;
                }
            }

            if (ops < mMaxOps && mRand.nextInt(2) == 0) {
                final Operator op = mOps.get(mRand.nextInt(mOps.size()));
                if (op.getArity() <= nstk) {
                    stack.push(op);
                    final String ret = genPostfix(stack, nstk - op.getArity() + 1, ops + 1, atoms);
                    stack.pop();
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }
    }

    public String generator() {
        return genPostfix(new Stack<Operator>(), 0, 0, 0);
    }
}
