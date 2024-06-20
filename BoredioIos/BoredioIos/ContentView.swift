import SwiftUI
import shared


struct ContentView: View {
    @State
    var viewModel: SimpleCounterViewModel?
    
    @State
    var counter : KotlinInt = 0;

	var body: some View {
        VStack {
            Text(String(counter.intValue))
                .task {
                    let viewModel = KotlinDependencies.shared.getSimpleCounterViewModel()
                    await withTaskCancellationHandler(
                        operation: {
                            self.viewModel = viewModel
                            for await counter in viewModel.count {
                                self.counter = counter
                            }
                        },
                        onCancel: {
                            viewModel.clear()
                            self.viewModel = nil
                        }
                    )
                }
            Button(action: {viewModel?.increment()}) {
                Text("++")
            }
            Button(action: {viewModel?.decrement()}) {
                Text("--")
            }
        }
        
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
        ContentView(viewModel: SimpleCounterViewModel())
	}
}
