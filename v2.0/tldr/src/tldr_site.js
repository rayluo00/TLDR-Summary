import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import './tldr_site.css';

class TLDR extends Component {
	constructor(props) {
		super(props);
		this.state = this.initState();
		this.initState = this.initState.bind(this);
		this.getUrl = this.getUrl.bind(this);
		this.printUrl = this.printUrl.bind(this);
	}

	getUrl(event) {
		var $ = require('jquery');
		var inputUrl = event.target.value;
		this.setState({url: inputUrl});

		console.log('POSTing');
		$.ajax({
			type: 'POST',
			headers: 'Access-Control-Allow-Origin',
			url: 'http://localhost:5000/',
			data: this.state.url
		});
	}

	printUrl(event) {
		var inputUrl = event.target.value;
		this.setState({url: inputUrl});
	}

	initState() {
		return {
			url: '',
			title: '',
			summary: ''
		};
	}

	render() {
		return (
			<div className='tdlr'>
				<div className='input_field'>
					<form onSubmit={this.getUrl}>
						<label>
							Website URL
							<input className='url_input' type='textbox'
							value={this.state.url} 
							onChange={this.printUrl}/>
						</label>
					</form>
				</div>
			</div>
		)
	}
}

ReactDOM.render(
	<TLDR />,	
	document.getElementById('root')
);

export default TLDR
