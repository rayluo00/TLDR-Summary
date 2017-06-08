import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import './tldr_site.css';

class TLDR extends Component {
	constructor(props) {
		super(props);
		this.state = this.initState();
		this.initState = this.initState.bind(this);
		this.getSentences = this.getSentences.bind(this);
		this.printUrl = this.printUrl.bind(this);
		this.displaySummary = this.displaySummary.bind(this);
	}

	displaySummary(summary) {
		var $ = require('jquery');

		if (summary !== '') {
			console.log('SUMMARY\n\n', summary);
			
			$('.summary').html(String(summary));
		}
	}

	getSentences(event) {
		event.preventDefault();

		var $ = require('jquery');
		var inputUrl = event.target.value;
		this.setState({url: inputUrl});
		var json_data = {'url': this.state.url}

		$.ajax({
			type: 'POST',
			contentType: 'application/json',
			headers: 'Access-Control-Allow-Origin',
			url: 'http://localhost:5000/',
			dataType: 'json',
			data: JSON.stringify(json_data),
			success: function (ret) {
				console.log('SUCCESS: ', ret);
			}
		});

		var in_field = document.getElementsByClassName('input_field');
		in_field[0].style.cssText = 'top: 25%; transition: all 0.75s linear;';

		$.ajax({
			type: 'GET',
			url: 'http://localhost:5000/getmethod',
			headers: 'Access-Control-Allow-Origin',
			dataType: 'json',
			success: function (summary){
				//console.log('GET: ', data);
				this.displaySummary(summary);
			}.bind(this)
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
					<form onSubmit={this.getSentences}>
						<label>
							Website URL
							<input className='url_input' type='textbox'
							value={this.state.url} 
							onChange={this.printUrl}/>
						</label>
					</form>
				</div>

				<div className='summary'></div>
			</div>
		)
	}
}

ReactDOM.render(
	<TLDR />,	
	document.getElementById('root')
);

export default TLDR
