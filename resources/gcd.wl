int wain(int a, int b){
	int temp = 0;
	if(a < b) {
		temp = a;
		a = b;
		b = temp;
	} else {}
	while(b != 0){
		temp = a%b;
		a = b;
		b = temp;
	}
	return a;
}
