package thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ForkJoinTest fTest = new ForkJoinTest();
		for (int j=0;j<100;j++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					List<Long> goodsList = new ArrayList<>();
					for (int i = 1; i <= 100; i++) {
						goodsList.add(Long.valueOf(i));
					}
					ForkJoinPool forkJoinPool = new ForkJoinPool();
					long now = System.currentTimeMillis();
					Future<Res> ret = forkJoinPool.submit(fTest.new GoodsDetailTask(goodsList, 0, goodsList.size()));
					long end = System.currentTimeMillis();
//					for (Entry<Long, String> entry : ret.getRet().entrySet()) {
//						System.out.println(entry.getKey() + "\t" + entry.getValue());
//					}
					try {
						System.out.println((end - now) + "| " + ret.get().getSum());
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					forkJoinPool.shutdown();
				}
			}).start();
			
		}
		
	}

	public Res process(List<Long> goodsIds) {
		Res res = new Res();
		Map<Long, String> ret = new HashMap<>();
		long sum = 0L;
		for (Long id : goodsIds) {
			ret.put(id, id + "goodsDetail");
			sum += id;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.setSum(sum);
		res.setRet(ret);
		return res;
	}

	class GoodsDetailTask extends RecursiveTask<Res> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static final int MAX = 10;
		List<Long> goodsList;
		int start;
		int end;

		public GoodsDetailTask(List<Long> goodsList, int start, int end) {
			// TODO Auto-generated constructor stub
			this.goodsList = goodsList;
			this.start = start;
			this.end = end;
		}

		@Override
		protected Res compute() {
			// TODO Auto-generated method stub
			if (end - start <= MAX) {
				Res ret = new Res();
				List<Long> partIds = new ArrayList<>();
				for (int i = start; i < end; i++) {
					partIds.add(goodsList.get(i));
				}
//				System.out.println(Thread.currentThread().getName());
				ret = process(partIds);

				return ret;
			}
			int middle = (start + end) / 2;
			GoodsDetailTask left = new GoodsDetailTask(goodsList, start, middle);
			GoodsDetailTask rifgt = new GoodsDetailTask(goodsList, middle, end);
			invokeAll(left, rifgt);
			left.fork();
			rifgt.fork();
			Res leftRet = left.join();
			Res rightRet = rifgt.join();

			Res total = new Res();
			total.setSum(leftRet.getSum() + rightRet.getSum());
			Map<Long, String> teMap = new HashMap<>();
			teMap.putAll(leftRet.getRet());
			teMap.putAll(rightRet.getRet());
			total.setRet(teMap);
			return total;
		}

	}
	
	class CallableTask implements Callable<Res> {
		private List<Long> goodsIds;
		
		public CallableTask(List<Long> ids) {
			// TODO Auto-generated constructor stub
			this.goodsIds = ids;
		}
		@Override
		public Res call() throws Exception {
			// TODO Auto-generated method stub
			return process(goodsIds);
		}
		
	}

}

class Res{
	private long sum;
	private Map<Long, String> ret;
	
	public long getSum() {
		return sum;
	}
	public void setSum(long sum) {
		this.sum = sum;
	}
	public Map<Long, String> getRet() {
		return ret;
	}
	public void setRet(Map<Long, String> ret) {
		this.ret = ret;
	}
	
}
